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


package org.kevoree.modeling.kotlin.generator

import java.io.File
import loader.xml.LoaderGenerator
import model.ModelGenerator
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.xmi.XMIResource
import scala.collection.JavaConversions._

import serializer.{SerializerJsonGenerator, SerializerGenerator}

/**
 * Created by IntelliJ IDEA.
 * User: Gregory NAIN
 * Date: 21/09/11
 * Time: 23:05
 */

/**
 * Generator class. Proposes several methods for generation of Model, Loader, Serializer from a EMF-<i>ecore</i> model.
 * @param ctx the generation context
 * @param ecoreFile the ecore model that implementation will be generated
 */
class Generator(ctx: GenerationContext, ecoreFile: File) {
  preProcess()

  private var preProcessPassed = false

  def preProcess() {
    val model = ctx.getEcoreModel(ecoreFile)

    //registering factories
    model.getContents.filter(e=>e.isInstanceOf[EPackage]).foreach {
      elem => ctx.registerFactory(elem.asInstanceOf[EPackage])
    }

    ctx.setBaseLocationForUtilitiesGeneration(ecoreFile)

  }


  /**
   * Triggers the generation of the given <i>ecore</i> file implementation.
   * @param modelVersion the version of the model (will be included in headers of generated files).
   */
  def generateModel(modelVersion: String) {

    val model = ctx.getEcoreModel(ecoreFile)

    checkModel(model)
    val modelGen = new ModelGenerator(ctx)

    System.out.println("Launching model generation")
    model.getContents.foreach {
      elem =>
        elem match {
          case pack: EPackage => {

            modelGen.process(pack, modelVersion, true)
          }
          case _ => println("No model generator for containerRoot element of class: " + elem.getClass)
        }
    }
    System.out.println("Done with model generation")
  }

  def checkModel(model: XMIResource) {

    model.getContents.foreach {
      content =>
        content match {
          case pack: EPackage => {
            if (pack.getNsPrefix == null || pack.getNsPrefix == "") {
              pack.setNsPrefix(pack.getName)
              System.err.println("The Metamodel package " + pack.getName + " does not have a Namespace Prefix. A namespace has been automatically used for generation.")
            }

            if (pack.getNsURI == null || pack.getNsURI == "") {
              pack.setNsURI("http://" + pack.getName)
              System.err.println("The Metamodel package " + pack.getName + " does not have a Namespace URI. A namespace has been automatically used for generation.")
              //throw new Exception("The base package "+pack.getName+" of the metamodel must contain an XML Namespace. Generation aborted.")
            }
          }
          case _ =>
        }
    }
  }


  def generateLoader() {

    val model = ctx.getEcoreModel(ecoreFile)

    System.out.println("Launching loader generation")
    val loaderGenerator = new LoaderGenerator(ctx)
    model.getContents.foreach {
      elem => elem match {
        case pack: EPackage => loaderGenerator.generateLoader(pack)
        case _ => println("No loader generator for containerRoot element of class: " + elem.getClass)
      }
    }
    System.out.println("Done with loader generation")
  }

  def generateSerializer() {

    val model = ctx.getEcoreModel(ecoreFile)

    System.out.println("Launching serializer generation")
    val serializerGenerator = new SerializerGenerator(ctx)
    model.getContents.foreach {
      elem => elem match {
        case pack: EPackage => serializerGenerator.generateSerializer(pack)
        case _ => println("No serializer generator for containerRoot element of class: " + elem.getClass)
      }
    }
    System.out.println("Done with serializer generation")
  }

  def generateJSONSerializer() {

    val model = ctx.getEcoreModel(ecoreFile)

    System.out.println("Launching json serializer generation")
    val serializerGenerator = new SerializerJsonGenerator(ctx)
    model.getContents.foreach {
      elem => elem match {
        case pack: EPackage => serializerGenerator.generateJsonSerializer(pack)
        case _ => println("No serializer generator for containerRoot element of class: " + elem.getClass)
      }
    }
    System.out.println("Done with serializer generation")
  }

}