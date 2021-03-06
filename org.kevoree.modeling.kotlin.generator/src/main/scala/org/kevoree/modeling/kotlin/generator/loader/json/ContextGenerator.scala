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


package org.kevoree.modeling.kotlin.generator.loader.json

import java.io.{File, PrintWriter}
import org.kevoree.modeling.kotlin.generator.{GenerationContext, ProcessorHelper}

/**
 * Created by IntelliJ IDEA.
 * User: Gregory NAIN
 * Date: 25/09/11
 * Time: 15:25
 */

class ContextGenerator(ctx:GenerationContext) {

  def generateContext() {
    val genLocation = ctx.getBaseLocationForUtilitiesGeneration.getAbsolutePath + File.separator + "loader" + File.separator
    ProcessorHelper.checkOrCreateFolder(genLocation)
    val localFile = new File(genLocation + "LoadingContext.kt")
     val pr = new PrintWriter(localFile,"utf-8")

    pr.println("package " + ProcessorHelper.fqn(ctx, ctx.getBasePackageForUtilitiesGeneration) + ".loader")
    pr.println()
    pr.println("class LoadingContext() {")
    pr.println()
    pr.println("\t\tvar loadedRoots : java.util.ArrayList<Any> = java.util.ArrayList<Any>()")
    pr.println()
    pr.println("\t\tval map : java.util.HashMap<String, Any> = java.util.HashMap<String, Any>()")
    pr.println()
    pr.println("\t\tval elementsCount : java.util.HashMap<String, Int> = java.util.HashMap<String, Int>()")
    pr.println()
    pr.println("\t\tval resolvers : java.util.ArrayList<ResolveCommand> = java.util.ArrayList<ResolveCommand>()")
    pr.println()
    pr.println("\t\tval stats : java.util.HashMap<String, Int> = java.util.HashMap<String, Int>()")
    pr.println()
    pr.println("}")

    pr.flush()
    pr.close()

  }

}