package io.kotest.plugin.intellij

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.application.BaseJavaApplicationCommandLineState
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.util.JavaParametersUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.ClassUtil

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
   override fun getTestLocator(): SMTestLocator = KotestSMTestLocator
}

object KotestSMTestLocator : SMTestLocator {
   override fun getLocation(protocol: String,
                            path: String,
                            project: Project,
                            scope: GlobalSearchScope): List<Location<PsiElement>> {
      val list = mutableListOf<Location<PsiElement>>()
      if (protocol == "kotest") {
         val (fqn, line) = path.split(':')
         val testClass = ClassUtil.findPsiClass(PsiManager.getInstance(project), fqn, null, true, scope)
         if (testClass != null) {
            val location: Location<PsiElement> = PsiLocation(testClass.project, testClass)
            list.add(location)
         }
      }
      return list
   }

   override fun getLocationCacheModificationTracker(project: Project): ModificationTracker = ModificationTracker.EVER_CHANGED
}
