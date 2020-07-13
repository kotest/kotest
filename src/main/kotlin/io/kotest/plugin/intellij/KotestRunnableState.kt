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
      val javaParameters = super.createJavaParameters()
      // this main class is what will be executed by intellij when someone clicks run
      // it is a main function that will launch the KotestConsoleRunner
      javaParameters.mainClass = "io.kotest.runner.console.LauncherKt"
      return javaParameters
   }

   override fun configureRTClasspath(javaParameters: JavaParameters, module: Module?) {
      javaParameters.classPath.addFirst(PathUtil.getJarPathForClass(Class.forName("io.kotest.runner.console.TeamCityConsoleWriter")))
   }

   override fun getScope(): TestSearchScope = TestSearchScope.WHOLE_PROJECT

   override fun passTempFile(parametersList: ParametersList, tempFilePath: String) {
      parametersList.add("-temp", tempFilePath)
   }

   override fun configureByModule(module: Module?): Boolean {
      return module != null
   }
}
