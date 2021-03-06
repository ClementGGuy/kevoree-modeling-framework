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


package org.kevoree.modeling.scala.generator.loader.xml

import org.eclipse.emf.ecore.{EPackage, EClass}
import java.io.{PrintWriter, File}
import org.kevoree.modeling.scala.generator.{GenerationContext, ProcessorHelper}

/**
 * Created by IntelliJ IDEA.
 * User: Gregory NAIN
 * Date: 29/09/11
 * Time: 17:53
 */

class InterfaceElementLoader(ctx : GenerationContext, genDir: String, genPackage: String, elementType: EClass, context: String, modelingPackage: EPackage, modelPackage : String) {


  def generateLoader() {

    //Creation of the generation dir
    ProcessorHelper.checkOrCreateFolder(genDir)
    val file = new File(genDir + "/" + elementType.getName + "Loader.scala")

    if (!file.exists()) { //Does not override existing file. Should have been removed before if required.
    //System.out.println("Generation of loader for " + elementType.getName)
    val subLoaders = generateSubs()

      val pr = new PrintWriter(file,"utf-8")
      //System.out.println("Classifier class:" + cls.getClass)

      pr.println("package " + genPackage)
      pr.println()
      pr.println("import xml.NodeSeq")
      pr.println("import scala.collection.JavaConversions._")
      //Import parent package (org.kevoree.sub => org.kevoree._)
      pr.println("import " + modelPackage + "._")
      pr.println("import " + genPackage.substring(0,genPackage.lastIndexOf(".")) + "._")
      pr.println()

      //Generates the Trait
      pr.print("trait " + elementType.getName + "Loader")
      if (subLoaders.size > 0) {
        var stringListSubLoaders = List[String]()
        subLoaders.foreach(sub => stringListSubLoaders = stringListSubLoaders ++ List(sub.getName + "Loader"))
        pr.println(stringListSubLoaders.mkString(" extends ", " with ", " {"))
      } else {
        pr.println("{")
      }

      pr.println("")

      generateLoadingMethod(pr)
      pr.println("")
      pr.println("}")

      pr.flush()
      pr.close()

      ProcessorHelper.formatScalaSource(file)

    }
  }


  private def generateSubs(): List[EClass] = {

    var listContainedElementsTypes = List[EClass]()
    ProcessorHelper.getAllConcreteSubTypes(elementType).foreach {
      concreteType =>
        if (!concreteType.isInterface) {
          val el = new BasicElementLoader(ctx, genDir, genPackage, concreteType, context, modelingPackage,modelPackage)
          el.generateLoader()
        } else {

          val el = new InterfaceElementLoader(ctx, genDir, genPackage, concreteType, context, modelingPackage,modelPackage)
          el.generateLoader()
        }
        if (!listContainedElementsTypes.contains(concreteType)) {
          listContainedElementsTypes = listContainedElementsTypes ++ List(concreteType)
        }
    }
    listContainedElementsTypes
  }

  private def generateLoadingMethod(pr: PrintWriter) {
    pr.println("def load" + elementType.getName + "Element(currentElementId : String, context : " + context + ") : " + ProcessorHelper.fqn(ctx,elementType) + " = {")

    pr.println("for(i <- 0 until context.xmiReader.getAttributeCount){")
      pr.println("val localName = context.xmiReader.getAttributeLocalName(i)")
      pr.println("val xsi = context.xmiReader.getAttributePrefix(i)")
      pr.println("if (localName == \"type\" && xsi==\"xsi\"){")
        pr.println("val loadedElement = context.xmiReader.getAttributeValue(i) match {")
          ProcessorHelper.getAllConcreteSubTypes(elementType).foreach {
            concreteType =>
              pr.println("case \"" + modelingPackage.getName + ":" + concreteType.getName + "\" => {")
              pr.println("load" + concreteType.getName + "Element(currentElementId,context)")
              pr.println("}")
          }
          pr.println("case _@e => {throw new UnsupportedOperationException(\"Processor for TypeDefinitions has no mapping for type:\" + localName);null}")
        pr.println("}")
        pr.println("return loadedElement")
      pr.println("}")
    pr.println("}")
    pr.println("null")
    pr.println("}")
  }

}