package com.beust.klaxon

import java.io.InputStream

public object Type {
    public val VALUE            : Int = 0
    public val LEFT_BRACE       : Int = 1
    public val RIGHT_BRACE      : Int = 2
    public val LEFT_BRACKET     : Int = 3
    public val RIGHT_BRACKET    : Int = 4
    public val COMMA            : Int = 5
    public val COLON            : Int = 6
    public val EOF              : Int = 42
}

class Token(val tokenType: Int, val value: Any?) {
    fun toString() : String {
        val v = if (value != null) { " (" + value + ")" } else {""}
        val result = tokenType.toString() + v
        return result
    }
}

public class Lexer(val inputStream : InputStream) {
    val bytes = inputStream.readBytes()
    val EOF = Token(Type.EOF, null)
    var index = 0

    fun isSpace(c: Char): Boolean {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t'
    }

    private fun nextChar() : Char {
        return bytes[index++].toChar()
    }

    private fun peekChar() : Char {
        return bytes[index].toChar()
    }

    private fun isDone() : Boolean {
        return index >= bytes.size
    }

    val BOOLEAN_LETTERS = hashSetOf('f', 'a', 'l', 's', 'e', 't', 'r', 'u')
    private fun isBooleanLetter(c: Char) : Boolean {
        return BOOLEAN_LETTERS.contains(Character.toLowerCase(c))
    }

    fun isValueLetter(c: Char) : Boolean {
        return c == '-' || c == '+' || c == '.' || c.isDigit() || isBooleanLetter(c)
    }

    fun nextToken() : Token {

        if (isDone()) {
            return EOF
        }

        var tokenType = Type.EOF
        var c = nextChar()
        var currentValue = StringBuilder()
        var jsonValue: Any? = null

        while (! isDone() && isSpace(c)) {
            c = nextChar()
        }

        if ('"' == c) {
            tokenType = Type.VALUE
            if (! isDone()) {
                c = nextChar()
                while (index < bytes.size && c != '"') {
                    currentValue.append(c)
                    if (c == '\\' && index < bytes.size) {
                        c = nextChar()
                        currentValue.append(c)
                    }
                    c = nextChar()
                }
                jsonValue = currentValue.toString()
            } else {
                throw RuntimeException("Unterminated string")
            }
        } else if ('{' == c) {
            tokenType = Type.LEFT_BRACE
        } else if ('}' == c) {
            tokenType = Type.RIGHT_BRACE
        } else if ('[' == c) {
            tokenType = Type.LEFT_BRACKET
        } else if (']' == c) {
            tokenType = Type.RIGHT_BRACKET
        } else if (':' == c) {
            tokenType = Type.COLON
        } else if (',' == c) {
            tokenType = Type.COMMA
        } else if (! isDone()) {
            while (isValueLetter(c)) {
                currentValue.append(c)
                if (! isValueLetter(peekChar())) {
                    break;
                } else {
                    c = nextChar()
                }
            }
            val v = currentValue.toString()
            if ("true".equals(v.toLowerCase())) {
                jsonValue = true
            } else if ("false".equals(v.toLowerCase())) {
                jsonValue = false
            } else {
                jsonValue = v.toLowerCase()
//                throw RuntimeException("Unexpected characted at position ${index}"
//                    + ": '${c} (${c.toInt()})'")
            }

            tokenType = Type.VALUE
        } else {
            tokenType = Type.EOF
        }

        return Token(tokenType, jsonValue)
    }
}
