package io.kotlintest.plugin.intellij

import com.intellij.execution.application.BaseJavaApplicationCommandLineState
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.util.JavaParametersUtil
import com.intellij.openapi.roots.OrderEnumerator

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

    params.mainClass = "io.kotlintest.ConsoleTestRunner"

    setupJavaParameters(params)

    module.module?.let {
      OrderEnumerator.orderEntries(it)
          .withoutLibraries()
          .withoutDepModules()
          .withoutSdk()
          .recursively()
          .classes()
          .pathsList
          .pathList
          .forEach {
            params.programParametersList.add("--sourceDirs", it)
          }

      params.programParametersList.add("--test", configuration.getTestName()!!)
      println("Params=$params")
      return params
    }
  }
}