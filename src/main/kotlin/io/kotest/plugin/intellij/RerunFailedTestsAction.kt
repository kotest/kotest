package io.kotest.plugin.intellij

import com.intellij.execution.Executor
import com.intellij.execution.actions.JavaRerunFailedTestsAction
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.SourceScope
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.module.Module
import io.kotest.plugin.intellij.run.KotestRunnableState

@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
class RerunFailedTestsAction(
   consoleView: ConsoleView,
   props: KotestTestConsoleProperties
) : JavaRerunFailedTestsAction(consoleView, props) {

   override fun getRunProfile(env: ExecutionEnvironment): MyRunProfile {
      val configuration = myConsoleProperties.configuration as KotestRunConfiguration
      val run = KotestRunnableState(env, configuration)
      return object : MyRunProfile(configuration) {

         override fun getModules(): Array<Module> {
            val scope = SourceScope.modules(configuration.modules)
            return if (scope == null) Module.EMPTY_ARRAY else scope.modulesToCompile
         }

         override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
            return run
         }
      }
   }
}
