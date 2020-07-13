package io.kotest.plugin.intellij

import com.intellij.execution.JavaTestFrameworkRunnableState
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.configurations.ParametersList
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.TestSearchScope
import com.intellij.openapi.module.Module
import com.intellij.util.PathUtil
import java.io.File

class KotestRunnableState(env: ExecutionEnvironment,
                          private val config: KotestConfiguration) : JavaTestFrameworkRunnableState<KotestConfiguration>(env) {

   override fun getForkMode(): String = "none"
   override fun getFrameworkId(): String = "kotest"
   override fun getFrameworkName(): String = "Kotest"
   override fun getConfiguration(): KotestConfiguration = config

   override fun passForkMode(forkMode: String?, tempFile: File?, parameters: JavaParameters?) {}

   override fun createJavaParameters(): JavaParameters {
      val params = super.createJavaParameters()
      // this main class is what will be executed by intellij when someone clicks run
      // it is a main function that will launch the KotestConsoleRunner
      params.mainClass = "io.kotest.runner.console.LauncherKt"

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

   override fun configureRTClasspath(javaParameters: JavaParameters, module: Module?) {
     javaParameters.classPath.addFirst(PathUtil.getJarPathForClass(Class.forName("io.kotest.runner.console.TeamCityConsoleWriter")))
     javaParameters.classPath.addFirst(PathUtil.getJarPathForClass(Class.forName("com.github.ajalt.clikt.core.CliktCommand")))
     javaParameters.classPath.addFirst(PathUtil.getJarPathForClass(Class.forName("com.github.ajalt.mordant.TermColors")))
   }

   override fun getScope(): TestSearchScope = TestSearchScope.WHOLE_PROJECT

   override fun passTempFile(parametersList: ParametersList, tempFilePath: String) {
      parametersList.add("-temp", tempFilePath)
   }

   override fun configureByModule(module: Module?): Boolean {
      return module != null
   }
}
