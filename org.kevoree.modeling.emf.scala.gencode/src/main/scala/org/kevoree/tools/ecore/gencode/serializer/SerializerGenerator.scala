/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 * 	Fouquet Francois
 * 	Nain Gregory
 */
/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 * Fouquet Francois
 * Nain Gregory
 */
/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 * Fouquet Francois
 * Nain Gregory
 */
/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 * Fouquet Francois
 * Nain Gregory
 */


package org.kevoree.tools.ecore.gencode.serializer

import scala.collection.JavaConversions._
import java.io.{File, FileOutputStream, PrintWriter}
import org.kevoree.tools.ecore.gencode.loader.RootLoader
import org.eclipse.emf.ecore.{EReference, EAttribute, EPackage, EClass}
import org.kevoree.tools.ecore.gencode.{GenerationContext, ProcessorHelper}

/**
 * Created by IntelliJ IDEA.
 * User: duke
 * Date: 02/10/11
 * Time: 20:55
 */

class SerializerGenerator(ctx: GenerationContext) {


  def generateSerializer(pack: EPackage) {

    val serializerGenBaseDir = ProcessorHelper.getPackageGenDir(ctx, pack) + "/serializer/"
    ProcessorHelper.checkOrCreateFolder(serializerGenBaseDir)

    val modelPackage = ProcessorHelper.fqn(ctx, pack)


    ctx.getRootContainerInPackage(pack) match {
      case Some(cls: EClass) => {
        val subs = generateSerializer(serializerGenBaseDir, modelPackage, pack.getName + ":" + cls.getName, cls, pack, true)
        generateDefaultSerializer(serializerGenBaseDir, modelPackage, cls, pack, subs)
      }
      case None => throw new UnsupportedOperationException("Root container not found. Returned one.")
    }
  }

  private def generateDefaultSerializer(genDir: String, packageName: String, root: EClass, rootXmiPackage: EPackage, sub: Set[String]) {

    val pr = new PrintWriter(new File(genDir + "ModelSerializer.scala"), "utf-8")
    pr.println("package " + packageName + ".serializer")
    pr.println("class ModelSerializer ")

    if (sub.size > 0) {
      pr.print(sub.mkString(" extends ", " with ", " "))
    }

    pr.println("{")

    pr.println()
    pr.println("\tdef serialize(o : Object) : scala.xml.Node = {")
    pr.println()
    pr.println("\t\to match {")
    pr.println("\t\t\tcase o : " + ProcessorHelper.fqn(ctx, root) + " => {")
    pr.println("\t\t\t\tval context = get" + root.getName + "XmiAddr(o,\"/\")")
    pr.println("\t\t\t\t" + root.getName + "toXmi(o,context)")
    pr.println("\t\t\t}")
    pr.println("\t\t\tcase _ => null")
    pr.println("\t\t}") //END MATCH
    pr.println("\t}") //END serialize method
    pr.println("}") //END TRAIT
    pr.flush()
    pr.close()
  }


  private def generateSerializer(genDir: String, packageName: String, refNameInParent: String, root: EClass, rootXmiPackage: EPackage, isRoot: Boolean = false): Set[String] = {
    var subSerializer = Set[String](root.getName + "Serializer")

    // ProcessorHelper.checkOrCreateFolder(genDir + "/serializer")
    //PROCESS SELF
    //System.out.println("[DEBUG] SerializerGenerator::generateSerializer => " + root.getName)

    val file = new File(genDir + root.getName + "Serializer.scala")

    //if(!file.exists()) {
    val pr = new PrintWriter(file, "utf-8")
    pr.println("package " + packageName + ".serializer")
    generateToXmiMethod(rootXmiPackage, root, pr, rootXmiPackage.getName + ":" + root.getName, isRoot)
    pr.flush()
    pr.close()

    //PROCESS SUB
    root.getEAllContainments.foreach {
      sub =>
        val subfile = new File(genDir + sub.getEReferenceType.getName + "Serializer.scala")
        if (!subfile.exists()) {
          val subpr = new PrintWriter(subfile, "utf-8")
          subpr.println("package " + packageName + ".serializer")
          generateToXmiMethod(rootXmiPackage, sub.getEReferenceType, subpr, sub.getName)
          subpr.flush()
          subpr.close()

          //¨PROCESS ALL SUB TYPE
          ProcessorHelper.getAllConcreteSubTypes(sub.getEReferenceType).foreach {
            subsubType =>
              if (subsubType != root) {
                //avoid looping in case of self-containment
                subSerializer = subSerializer ++ generateSerializer(genDir, packageName, sub.getName, subsubType, rootXmiPackage)
              }
          }
          if (sub.getEReferenceType != root) {
            //avoid looping in case of self-containment
            subSerializer = subSerializer ++ generateSerializer(genDir, packageName, sub.getName, sub.getEReferenceType, rootXmiPackage)
          }
        }

    }
    // }

    subSerializer

  }


  private def getGetter(name: String): String = {
    "get" + name.charAt(0).toUpper + name.substring(1)
  }


  private def generateToXmiMethod(pack: EPackage, cls: EClass, buffer: PrintWriter, refNameInParent: String, isRoot: Boolean = false) = {
    val packageOfModel = ProcessorHelper.fqn(ctx, pack)

    buffer.println("import " + packageOfModel + "._")
    buffer.println()
    buffer.println("trait " + cls.getName + "Serializer ")


    var stringListSubSerializers = Set[Tuple2[String, String]]()

    if (cls.getEAllContainments.size > 0) {
      cls.getEAllContainments.foreach {
        contained =>
          if (contained.getEReferenceType != cls) {
            stringListSubSerializers = stringListSubSerializers ++ Set(Tuple2(ProcessorHelper.fqn(ctx, contained.getEReferenceType), contained.getEReferenceType.getName)) // + "Serializer")
          }
      }
    }
    val subTypes = ProcessorHelper.getDirectConcreteSubTypes(cls)
    if (subTypes.size > 0) {
      subTypes.foreach {
        sub =>
          stringListSubSerializers = stringListSubSerializers ++ Set(Tuple2(ProcessorHelper.fqn(ctx, sub), sub.getName)) // + "Serializer")
      }
    }

    /*
if (stringListSubSerializers.size > 0) {
buffer.print(stringListSubSerializers.mkString(" extends ", " with ", " "))
}       */

    buffer.println("{")

    buffer.println()


    stringListSubSerializers.foreach {
      sub =>
        buffer.println("def get" + sub._2 + "XmiAddr(o : " + sub._1 + ",previousAddr : String) : scala.collection.mutable.Map[Object,String]") //PRINT ABSTRACT USEFULL METHOD
        buffer.println("def " + sub._2 + "toXmi(o :" + sub._1 + ",refNameInParent : String, addrs : scala.collection.mutable.Map[Object,String]) : scala.xml.Node")
    }
    buffer.println()
    buffer.println()
    buffer.println()


    //GENERATE GET XMI ADDR
    //System.out.println("[DEBUG] SerializerGen::" + cls)
    buffer.println("\tdef get" + cls.getName + "XmiAddr(selfObject : " + ProcessorHelper.fqn(ctx, cls) + ",previousAddr : String): scala.collection.mutable.Map[Object,String] = {")
    buffer.println("\t\tvar subResult = new scala.collection.mutable.HashMap[Object,String]()")

    buffer.println("\t\tif(previousAddr == \"/\"){ subResult.put(selfObject,\"/\") }\n")

    buffer.println("\t\tvar i = 0")
    cls.getEAllContainments.foreach {
      subClass =>
        subClass.getUpperBound match {
          case 1 => {
            if (subClass.getLowerBound == 0) {
              buffer.println()
              buffer.println("\t\tselfObject." + getGetter(subClass.getName) + ".map{ sub =>")
              buffer.println("\t\t\tsubResult +=  sub -> (previousAddr+\"/@" + subClass.getName + "\" ) ")
              buffer.println("\t\t\tsubResult ++= get" + subClass.getEReferenceType.getName + "XmiAddr(sub,previousAddr+\"/@" + subClass.getName + "\")")
              buffer.println("\t\t}")
            } else {
              buffer.println()
              //buffer.println("\t\t + ".map{ sub =>")
              buffer.println("\t\tsubResult +=  selfObject." + getGetter(subClass.getName) + " -> (previousAddr+\"/@" + subClass.getName + "\" ) ")
              buffer.println("\t\tsubResult ++= get" + subClass.getEReferenceType.getName + "XmiAddr(selfObject." + getGetter(subClass.getName) + ",previousAddr+\"/@" + subClass.getName + "\")")
              //buffer.println("\t\t}")
            }
          }
          case -1 => {
            buffer.println("\t\ti=0")
            buffer.println("\t\tselfObject." + getGetter(subClass.getName) + ".foreach{ sub => ")
            buffer.println("\t\t\tsubResult +=  sub -> (previousAddr+\"/@" + subClass.getName + ".\"+i) ")
            buffer.println("\t\t\tsubResult ++= get" + subClass.getEReferenceType.getName + "XmiAddr(sub,previousAddr+\"/@" + subClass.getName + ".\"+i)")
            buffer.println("\t\t\ti=i+1")
            buffer.println("\t\t}")
            buffer.println()
          }
        }
    }

    buffer.println()

    if (subTypes.size > 0) {
      buffer.println("\t\tselfObject match {")
      subTypes.foreach {
        subType =>
          buffer.println("\t\t\tcase o:" + ProcessorHelper.fqn(ctx, subType) + " =>subResult ++= get" + subType.getName + "XmiAddr(selfObject.asInstanceOf[" + ProcessorHelper.fqn(ctx, subType) + "],previousAddr)")
      }

      buffer.println("\t\t\tcase _ => \n")//throw new InternalError(\""+ cls.getName +"Serializer did not match anything for selfObject class name: \" + selfObject.getClass.getName)")
      buffer.println("\t\t}")
    }



    buffer.println("\t\tsubResult")
    buffer.println("\t}")


    if (isRoot) {
      buffer.println("\tdef " + cls.getName + "toXmi(selfObject : " + ProcessorHelper.fqn(ctx, cls) + ", addrs : scala.collection.mutable.Map[Object,String]) : scala.xml.Node = {")
    } else {
      buffer.println("\tdef " + cls.getName + "toXmi(selfObject : " + ProcessorHelper.fqn(ctx, cls) + ",refNameInParent : String, addrs : scala.collection.mutable.Map[Object,String]) : scala.xml.Node = {")
    }

    buffer.println("\t\tselfObject match {")
    var subtypesList = ProcessorHelper.getDirectConcreteSubTypes(cls)
    //subtypesList.s
   /*
    System.out.println("[DEBUG]Prior sorting:" + subtypesList.map{c=>c.getName}.mkString("[",",","]"))
    subtypesList = subtypesList.sortWith{(e1,e2) => {
      System.out.println("[DEBUG]Sorting (" + e1.getName + "," + e2.getName + ") result e1.ST.contains(e2): " + e1.getEAllSuperTypes.contains(e2))
      e1.asInstanceOf[EClass].getEAllSuperTypes.contains(e2)
    }}
    System.out.println("[DEBUG]After sorting:" + subtypesList.map{c=>c.getName}.mkString("[",",","]"))
    */
    subtypesList.foreach {
      subType =>
        buffer.println("\t\t\tcase o:" + ProcessorHelper.fqn(ctx, subType) + " => " + subType.getName + "toXmi(selfObject.asInstanceOf[" + ProcessorHelper.fqn(ctx, subType) + "],refNameInParent,addrs)")
    }

    buffer.println("\t\t\tcase _ => {")
   // buffer.println("\t\t\t\tSystem.out.println(\"[WARNING]\" + getClass.getName + \": \" + selfObject.getClass.getName + \" did not match anything in " + cls.getName + "toXMI\")")
    buffer.println("\t\t\tnew scala.xml.Node {")
    if (!isRoot) {
      buffer.println("\t\t\t\t\tdef label = refNameInParent")
    } else {
      buffer.println("\t\t\t\t\tdef label = \"" + refNameInParent + "\"")
    }


    buffer.println("\t\t\t\t\tdef child = {")
    buffer.println("\t\t\t\t\t\tvar subresult: scala.collection.mutable.ListBuffer[scala.xml.Node] = new scala.collection.mutable.ListBuffer[scala.xml.Node]()")

    cls.getEAllContainments.foreach {
      subClass =>

        subClass.getUpperBound match {
          case 1 => {
            if (subClass.getLowerBound == 0) {
              buffer.println("\t\t\t\t\t\tselfObject." + getGetter(subClass.getName) + ".map { so => ")
              buffer.println("\t\t\t\t\t\t\tsubresult += (" + subClass.getEReferenceType.getName + "toXmi(so,\"" + subClass.getName + "\",addrs))")
              buffer.println("\t\t\t\t\t\t}")
            } else {
              //buffer.println("\t\t\t\t\t\tselfObject." + getGetter(subClass.getName) + ".map { so => ")
              buffer.println("\t\t\t\t\t\tsubresult += (" + subClass.getEReferenceType.getName + "toXmi(selfObject." + getGetter(subClass.getName) + ",\"" + subClass.getName + "\",addrs))")
              // buffer.println("\t\t\t\t\t\t}")
            }
          }
          case -1 => {
            buffer.println("\t\t\t\t\t\tselfObject." + getGetter(subClass.getName) + ".foreach { so => ")
            buffer.println("\t\t\t\t\t\t\tsubresult += (" + subClass.getEReferenceType.getName + "toXmi(so,\"" + subClass.getName + "\",addrs))")
            buffer.println("\t\t\t\t\t\t}")
          }

        }

    }

    buffer.println("\t\t\t\t\t\tsubresult")
    buffer.println("\t\t\t\t\t}")





    if (isRoot || cls.getEAllAttributes.size() > 0 || cls.getEAllReferences.filter(eref => !cls.getEAllContainments.contains(eref)).size > 0) {
      buffer.println("\t\t\t\t\toverride def attributes  : scala.xml.MetaData =  { ")
      buffer.println("\t\t\t\t\t\tvar subAtts : scala.xml.MetaData = scala.xml.Null")
      if (isRoot) {
        buffer.println("\t\t\t\t\t\tsubAtts=subAtts.append(new scala.xml.UnprefixedAttribute(\"xmlns:" + cls.getEPackage.getNsPrefix + "\",\"" + cls.getEPackage.getNsURI + "\",scala.xml.Null))")
        buffer.println("\t\t\t\t\t\tsubAtts=subAtts.append(new scala.xml.UnprefixedAttribute(\"xmlns:xsi\",\"http://wwww.w3.org/2001/XMLSchema-instance\",scala.xml.Null))")
        buffer.println("\t\t\t\t\t\tsubAtts=subAtts.append(new scala.xml.UnprefixedAttribute(\"xmi:version\",\"2.0\",scala.xml.Null))")
        buffer.println("\t\t\t\t\t\tsubAtts=subAtts.append(new scala.xml.UnprefixedAttribute(\"xmlns:xml\",\"http://www.omg.org/XMI\",scala.xml.Null))")

      }


      //if (cls.isAbstract || cls.isInterface) {
      //buffer.println("selfObject match {")
      //ProcessorHelper.getConcreteSubTypes(cls).foreach {
      //concreteType =>
      //buffer.println("case concreteT : " + concreteType.getName + " => {")
      buffer.println("\t\t\t\t\t\tsubAtts=subAtts.append(new scala.xml.UnprefixedAttribute(\"xsi:type\",\"" + cls.getEPackage.getName + ":" + cls.getName + "\",scala.xml.Null))")
      // buffer.println("}")
      //}
      //buffer.println("case _ =>")
      //buffer.println("}")
      //}
      cls.getEAllAttributes.foreach {
        att =>
          att.getUpperBound match {
            case 1 => {
              att.getLowerBound match {
                /*
           case 0 => {
             buffer.println("selfObject." + getGetter(att.getName) + ".map{sub =>")
             buffer.println("subAtts= subAtts.append(new scala.xml.UnprefixedAttribute(\"" + att.getName + "\",sub.toString,scala.xml.Null))")
             buffer.println("}")
           }     */
                case _ => {
                  buffer.println("\t\t\t\t\t\tif(selfObject." + getGetter(att.getName) + ".toString != \"\"){")
                  buffer.println("\t\t\t\t\t\t\tsubAtts= subAtts.append(new scala.xml.UnprefixedAttribute(\"" + att.getName + "\",selfObject." + getGetter(att.getName) + ".toString,scala.xml.Null))")
                  buffer.println("\t\t\t\t\t\t}")
                }
              }


            }
            case -1 => println("WTF!")
          }

      }
      cls.getEAllReferences.filter(eref => !cls.getEAllContainments.contains(eref)).foreach {
        ref =>
          ref.getUpperBound match {
            case 1 => {
              ref.getLowerBound match {
                case 0 => {
                  buffer.println("\t\t\t\t\t\tselfObject." + getGetter(ref.getName) + ".map{sub =>")
                  buffer.println("\t\t\t\t\t\t\tsubAtts= subAtts.append(new scala.xml.UnprefixedAttribute(\"" + ref.getName + "\",addrs.get(sub).getOrElse{throw new Exception(\"KMF Serialization error : non contained reference\");\"non contained reference\"},scala.xml.Null))")
                  buffer.println("\t\t\t\t\t\t}")
                }
                case 1 => {


                  if (ref.getEOpposite != null) {
                    if (ref.getEOpposite.getUpperBound != -1) {
                      buffer.println("\t\t\t\t\t\tsubAtts= subAtts.append(new scala.xml.UnprefixedAttribute(\"" + ref.getName + "\",addrs.get(selfObject." + getGetter(ref.getName) + ").getOrElse{throw new Exception(\"KMF Serialization error : non contained reference\");\"non contained reference\"},scala.xml.Null))")
                    }
                  } else {
                    buffer.println("\t\t\t\t\t\tsubAtts= subAtts.append(new scala.xml.UnprefixedAttribute(\"" + ref.getName + "\",addrs.get(selfObject." + getGetter(ref.getName) + ").getOrElse{throw new Exception(\"KMF Serialization error : non contained reference\");\"non contained reference\"},scala.xml.Null))")
                  }

                }
              }


            }
            case _ => {
              buffer.println("\t\t\t\t\t\tvar subadrs" + ref.getName + " : List[String] = List()")
              buffer.println("\t\t\t\t\t\tselfObject." + getGetter(ref.getName) + ".foreach{sub =>")
              buffer.println("\t\t\t\t\t\t\tsubadrs" + ref.getName + " = subadrs" + ref.getName + " ++ List(addrs.get(sub).getOrElse{throw new Exception(\"KMF Serialization error : non contained reference\");\"non contained reference\"})")
              buffer.println("\t\t\t\t\t\t}")
              buffer.println("\t\t\t\t\t\tif(subadrs" + ref.getName + ".size > 0){")
              buffer.println("\t\t\t\t\t\t\tsubAtts= subAtts.append(new scala.xml.UnprefixedAttribute(\"" + ref.getName + "\",subadrs" + ref.getName + ".mkString(\" \"),scala.xml.Null))")
              buffer.println("\t\t\t\t\t\t}")
            }
          }
      }
      buffer.print("\t\t\t\t\t\tsubAtts")
      buffer.println("\t\t\t\t\t}")
    }

    buffer.println("\t\t\t\t}")

    buffer.println("\t\t\t}") //End new Node
    buffer.println("\t\t}") //End MATCH CASE
    buffer.println("\t}") //END TO XMI
    buffer.println("}") //END TRAIT
  }


}