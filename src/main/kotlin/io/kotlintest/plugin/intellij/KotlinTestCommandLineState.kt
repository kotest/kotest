package io.kotlintest.plugin.intellij

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
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.PathUtil
import io.kotlintest.runner.console.KotlinTestConsoleRunner
import net.sourceforge.argparse4j.inf.ArgumentParser

class KotlinTestCommandLineState(environment: ExecutionEnvironment, configuration: KotlinTestRunConfiguration) :
    BaseJavaApplicationCommandLineState<KotlinTestRunConfiguration>(environment, configuration) {

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
    // it is a main function that will launch the KotlinTestConsoleRunner
    params.mainClass = "io.kotlintest.runner.console.LauncherKt"

    // the module assigned to a configuration contains the classpath deps defined in the users build
    // but we must also include any dependencies we need that the user won't explicitly depend on, such
    // as the console runner, and args4j
    val jars = listOf(
        PathUtil.getJarPathForClass(KotlinTestConsoleRunner::class.java),
        PathUtil.getJarPathForClass(ArgumentParser::class.java)
    )
    params.classPath.addAll(jars)

    // these two parameters are required by the console runner so it knows what test to execute
    params.programParametersList.add("--spec", configuration.getSpecName()!!)
    // test can actually be left out if you want to run the entire spec
    val testName = configuration.getTestName()
    if (testName != null && testName.isNotBlank())
      params.programParametersList.add("--test", testName)
    return params
  }

  override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
    val processHandler = startProcess()
    val props = KotlinTestSMTConsoleProperties(configuration, executor)
    props.setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false)
    props.setIfUndefined(TestConsoleProperties.SCROLL_TO_SOURCE, true)
    props.setIfUndefined(TestConsoleProperties.TRACK_RUNNING_TEST, true)
    val console = SMTestRunnerConnectionUtil.createAndAttachConsole("kotlintest", processHandler, props)
    return DefaultExecutionResult(console, processHandler, *createActions(console, processHandler, executor))
  }
}

class KotlinTestSMTConsoleProperties(config: KotlinTestRunConfiguration, executor: Executor) : SMTRunnerConsoleProperties(config, "kotlintest", executor) {
  override fun getTestLocator(): SMTestLocator = KotlinTestSMTestLocator
}

object KotlinTestSMTestLocator : SMTestLocator {
  override fun getLocation(protocol: String,
                           path: String,
                           project: Project,
                           scope: GlobalSearchScope): List<Location<PsiElement>> {
    return if (protocol == "kotlintest") {
      val cache = project.getComponent(ElementLocationCache::class.java)
      val (fqn, line) = path.split(':')
      val element = cache.element(fqn, line.toInt())
      if (element == null) emptyList<Location<PsiElement>>() else listOf(PsiLocation(element.navigationElement))
    } else emptyList()
  }

  override fun getLocationCacheModificationTracker(project: Project): ModificationTracker = ModificationTracker.EVER_CHANGED
}