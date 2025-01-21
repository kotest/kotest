package io.kotest.plugin.intellij.console

import com.intellij.execution.Executor
import com.intellij.execution.Location
import com.intellij.execution.testframework.JavaAwareTestConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration
import com.intellij.pom.Navigatable
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.KotestTestLocator
import javax.swing.tree.TreeSelectionModel

class KotestSMTRunnerConsoleProperties(
   conf: ExternalSystemRunConfiguration,
   executor: Executor
) : SMTRunnerConsoleProperties(conf, Constants.FrameworkName, executor) {

   override fun getTestLocator(): SMTestLocator = KotestTestLocator()

   /**
    * Besides that, your approach may work, but you need to use so called "IdBased" mode. In regular mode node can be either suite (branch) or test (leaf). Suits can't be empty. And when test failed all its ancestors are marked so. Any started test belongs to currenly opened suite hence only sequental execution is possible.
    *
    * In "IdBased" mode there is no difference between tests and suites. Any test may have parent and root should have parentNodeId=0, so you can easly start 3 tests then finish second one, then first one etc.
    *
    * To enable this mode you need to set ``isIdBasedTestTree()`` to true in your ```SMTRunnerConsoleProperties```.
    *
    * You may also want to subscribe to ```SMTRunnerEventsListener.TEST_STATUS``` because in this mode parent tests are not marked red when child test failed (see ```PythonTRunnerConsoleProperties``` for example).
    *
    * https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000389550/comments/115000330464
    */
   override fun isIdBasedTestTree(): Boolean = true
   override fun isEditable(): Boolean = true
   override fun isPrintTestingStartedTime(): Boolean = true
   override fun getSelectionMode(): Int = TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
   override fun getErrorNavigatable(location: Location<*>, stacktrace: String): Navigatable? {
      return JavaAwareTestConsoleProperties.getStackTraceErrorNavigatable(location, stacktrace)
   }
}
