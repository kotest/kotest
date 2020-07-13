package io.kotest.plugin.intellij

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.application.BaseJavaApplicationCommandLineState
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.impl.ConsoleBuffer
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.JavaAwareTestConsoleProperties
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.util.JavaParametersUtil
import com.intellij.psi.search.GlobalSearchScope

class KotestCommandLineState(environment: ExecutionEnvironment, configuration: KotestConfiguration) :
   BaseJavaApplicationCommandLineState<KotestConfiguration>(environment, configuration) {

   override fun createJavaParameters(): JavaParameters {

      val params = JavaParameters()
      params.isUseClasspathJar = true

      val configurationModule = configuration.configurationModule


      setupJavaParameters(params)

//      for (ext in RunConfigurationExtension.EP_NAME.extensionList) {
//         // ext.updateJavaParameters(configuration, javaParameters, runnerSettings, environment.executor)
//      }

      if (ConsoleBuffer.useCycleBuffer()) {
         javaParameters.vmParametersList.addProperty("idea.test.cyclic.buffer.size", ConsoleBuffer.getCycleBufferSize().toString())
      }

      val pathType = JavaParameters.JDK_AND_CLASSES_AND_TESTS
      val jreHome = if (myConfiguration.isAlternativeJrePathEnabled) myConfiguration.alternativeJrePath else null
      if (configurationModule != null) {
         JavaParametersUtil.configureModule(configurationModule, javaParameters, pathType, jreHome)
      } else {
         JavaParametersUtil.configureProject(configuration.project, javaParameters, pathType, jreHome)
      }


      // this main class is what will be executed by intellij when someone clicks run
      // it is a main function that will launch the KotestConsoleRunner
      params.mainClass = "io.kotest.runner.console.LauncherKt"

      // the module assigned to a configuration contains the classpath deps defined in the users build
      // but we must also include any dependencies we need that the user won't explicitly depend on, such
      // as the console runner, clickt, mordant and classgraph.
//      val jars = listOf(
//         PathUtil.getJarPathForClass(KotestConsoleRunner::class.java),
//         PathUtil.getJarPathForClass(ArgumentParser::class.java)
//      )
//      params.classPath.addAll(jars)

      val packageName = configuration.getPackageName()
      if (packageName != null && packageName.isNotBlank())
         params.programParametersList.add("--package", packageName)

      // spec can be omitted if you want to run all tests in a module
      val specName = configuration.getSpecName()
      if (specName != null && specName.isNotBlank())
         params.programParametersList.add("--spec", specName)

      // test can be omitted if you want to run the entire spec or package
      val testName = configuration.getTestPath()
      if (testName != null && testName.isNotBlank())
         params.programParametersList.add("--testpath", testName)
      return params
   }

   override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
      val processHandler = startProcess()
      val props = KotestSMTConsoleProperties(configuration, executor)
      props.setIfUndefined(TestConsoleProperties.HIDE_IGNORED_TEST, false)
      props.setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false)
      props.setIfUndefined(TestConsoleProperties.SCROLL_TO_STACK_TRACE, true)
      props.setIfUndefined(TestConsoleProperties.SCROLL_TO_SOURCE, true)
      props.setIfUndefined(TestConsoleProperties.TRACK_RUNNING_TEST, true)
      props.setIfUndefined(TestConsoleProperties.SHOW_STATISTICS, true)
      props.setIfUndefined(TestConsoleProperties.INCLUDE_NON_STARTED_IN_RERUN_FAILED, true)
      val console = SMTestRunnerConnectionUtil.createAndAttachConsole("kotest", processHandler, props)
      return DefaultExecutionResult(console, processHandler, *createActions(console, processHandler, executor))
   }
}

class KotestSMTConsoleProperties(config: KotestConfiguration,
                                 executor: Executor) : JavaAwareTestConsoleProperties<KotestConfiguration>("Kotest", config, executor) {
   init {
      isPrintTestingStartedTime = true
   }

   override fun getTestLocator(): SMTestLocator = KotestTestLocator

   override fun initScope(): GlobalSearchScope {
      return GlobalSearchScope.allScope(project)
   }

   override fun createRerunFailedTestsAction(consoleView: ConsoleView): AbstractRerunFailedTestsAction? {
      return RerunFailedTestsAction(consoleView, this)
   }
}

