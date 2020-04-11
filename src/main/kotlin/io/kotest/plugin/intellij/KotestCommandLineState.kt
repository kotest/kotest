package io.kotest.plugin.intellij

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.application.BaseJavaApplicationCommandLineState
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.util.JavaParametersUtil

class KotestCommandLineState(environment: ExecutionEnvironment, configuration: KotestRunConfiguration) :
    BaseJavaApplicationCommandLineState<KotestRunConfiguration>(environment, configuration) {

   override fun createJavaParameters(): JavaParameters {

      val params = JavaParameters()
      params.isUseClasspathJar = true

      val module = configuration.configurationModule
      val jreHome = if (myConfiguration.isAlternativeJrePathEnabled) {
         myConfiguration.alternativeJrePath
      } else {
         null
      }

      val pathType = JavaParameters.JDK_AND_CLASSES_AND_TESTS
      JavaParametersUtil.configureModule(module, params, pathType, jreHome)
      setupJavaParameters(params)

      // this main class is what will be executed by intellij when someone clicks run
      // it is a main function that will launch the KotestConsoleRunner
      params.mainClass = "io.kotest.runner.console.LauncherKt"

      // the module assigned to a configuration contains the classpath deps defined in the users build
      // but we must also include any dependencies we need that the user won't explicitly depend on, such
      // as the console runner, and args4j
//    val jars = listOf(
//        PathUtil.getJarPathForClass(KotestConsoleRunner::class.java),
//        PathUtil.getJarPathForClass(ArgumentParser::class.java)
//    )
//    params.classPath.addAll(jars)

      // these two parameters are required by the console runner so it knows what test to execute
      params.programParametersList.add("--spec", configuration.getSpecName()!!)
      // test can actually be left out if you want to run the entire spec
      val testName = configuration.getTestName()
      if (testName != null && testName.isNotBlank())
         params.programParametersList.add("--testpath", testName)
      return params
   }

   override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
      val processHandler = startProcess()
      val props = KotestSMTConsoleProperties(configuration, executor)
      props.setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false)
      props.setIfUndefined(TestConsoleProperties.SCROLL_TO_SOURCE, true)
      props.setIfUndefined(TestConsoleProperties.TRACK_RUNNING_TEST, true)
      val console = SMTestRunnerConnectionUtil.createAndAttachConsole("kotest", processHandler, props)
      return DefaultExecutionResult(console, processHandler, *createActions(console, processHandler, executor))
   }
}

class KotestSMTConsoleProperties(config: KotestRunConfiguration,
                                 executor: Executor) : SMTRunnerConsoleProperties(config, "kotest", executor) {
   init {
      isPrintTestingStartedTime = true
   }

   override fun getTestLocator(): SMTestLocator = KotestSMTestLocator
}

