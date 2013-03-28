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
package org.kevoree.modeling.kotlin.generator.factories

import org.kevoree.modeling.kotlin.generator.{ProcessorHelper, GenerationContext}
import java.io.{PrintWriter, File}
import scala.collection.JavaConversions._

/*
* Author : Gregory Nain (developer.name@uni.lu)
* Date : 28/03/13
*/
class FactoryGenerator(ctx:GenerationContext) {

  def generateMainFactory() {
    ProcessorHelper.checkOrCreateFolder(ctx.getBaseLocationForUtilitiesGeneration.getAbsolutePath + File.separator + "factory")

    val genFile = new File(ctx.getBaseLocationForUtilitiesGeneration.getAbsolutePath + File.separator + "factory" + File.separator + "MainFactory.kt")
    val pr = new PrintWriter(genFile, "utf-8")
    pr.println("package " + ProcessorHelper.fqn(ctx,ctx.getBasePackageForUtilitiesGeneration) + ".serializer")
    pr.println("class MainFactory {")
    pr.println("")
    pr.println("private var factories : java.util.HashMap<String, Any> = java.util.HashMap<String, Any>();")
    pr.println("")
    pr.println("{")
    ctx.packageFactoryMap.entrySet().foreach { entry =>
      pr.println("factories.put(\"" + entry.getKey + "\", " + entry.getKey + ".impl.Default" + entry.getValue.substring(entry.getValue.lastIndexOf(".")+1, entry.getValue.length) + "())")
    }
    pr.println("}")


    pr.println("fun getFactoryForPackage( pack : String) : Any? {")
    pr.println("return factories.get(pack)")
    pr.println("}")

    ctx.packageFactoryMap.entrySet().foreach { entry =>
      pr.println("fun get" + entry.getValue.substring(entry.getValue.lastIndexOf(".")+1, entry.getValue.length) + "() : "+entry.getValue+" {")
      pr.println("return factories.get(\"" + entry.getKey + "\")!! as " + entry.getValue)
      pr.println("}")
      pr.println("")
      pr.println("fun set" + entry.getValue.substring(entry.getValue.lastIndexOf(".")+1, entry.getValue.length) + "( fct : "+entry.getValue+") {")
      pr.println("factories.put(\"" + entry.getKey + "\",fct)")
      pr.println("}")
      pr.println("")
    }
    pr.println("")

    pr.println("}")
    pr.flush()
    pr.close()
  }

}