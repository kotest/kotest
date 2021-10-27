package io.kotest.plugin.intellij

import com.intellij.execution.Executor
import com.intellij.execution.testframework.JavaAwareTestConsoleProperties
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.ui.ConsoleView
import com.intellij.psi.search.GlobalSearchScope

class KotestTestConsoleProperties(config: KotestConfiguration,
                                  executor: Executor) : JavaAwareTestConsoleProperties<KotestConfiguration>(Constants.FrameworkName, config, executor) {
   init {
      isPrintTestingStartedTime = true
      isIdBasedTestTree = false
   }

   override fun getTestLocator(): SMTestLocator = KotestTestLocator

   override fun isDebug(): Boolean {
      return false
   }

   override fun initScope(): GlobalSearchScope {
      val sourceScope = configuration.testSearchScope.getSourceScope(configuration)
      return sourceScope?.globalSearchScope ?: GlobalSearchScope.allScope(project)
   }

   override fun createRerunFailedTestsAction(consoleView: ConsoleView): AbstractRerunFailedTestsAction? {
      return RerunFailedTestsAction(consoleView, this)
   }
}
