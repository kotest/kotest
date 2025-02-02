package io.kotest.plugin.intellij.console

import com.intellij.execution.Executor
import com.intellij.execution.testframework.JavaAwareTestConsoleProperties
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.ui.ConsoleView
import com.intellij.psi.search.GlobalSearchScope
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.run.KotestRunConfiguration
import io.kotest.plugin.intellij.locations.KotestTestLocator
import io.kotest.plugin.intellij.RerunFailedTestsAction

@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
class KotestTestConsoleProperties(
   config: KotestRunConfiguration,
   executor: Executor
) : JavaAwareTestConsoleProperties<KotestRunConfiguration>(Constants.FRAMEWORK_NAME, config, executor) {
   init {
      isPrintTestingStartedTime = true
   }

   override fun getTestLocator(): SMTestLocator = KotestTestLocator()

   override fun initScope(): GlobalSearchScope {
      val sourceScope = configuration.testSearchScope.getSourceScope(configuration)
      return sourceScope?.globalSearchScope ?: GlobalSearchScope.allScope(project)
   }

   override fun createRerunFailedTestsAction(consoleView: ConsoleView): AbstractRerunFailedTestsAction? {
      return RerunFailedTestsAction(consoleView, this)
   }
}
