package io.kotest.plugin.intellij.run

import com.intellij.execution.Executor
import com.intellij.execution.JavaTestFrameworkRunnableState
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.configurations.ParametersList
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.TestSearchScope
import com.intellij.openapi.module.Module
import com.intellij.util.PathUtil
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.KotestConfiguration
import java.io.File

class KotestRunnableState(env: ExecutionEnvironment,
                          private val config: KotestConfiguration) : JavaTestFrameworkRunnableState<KotestConfiguration>(env) {

   private val mainClass = "io.kotest.launcher.LauncherKt"

   private val requiredJars = listOf(
      "io.kotest.launcher.TeamCityMessages",
      "com.github.ajalt.clikt.core.CliktCommand",
      "com.github.ajalt.mordant.TermColors"
   )

   override fun getForkMode(): String = "none"
   override fun getFrameworkId(): String = Constants.FrameworkId
   override fun getFrameworkName(): String = Constants.FrameworkName
   override fun getConfiguration(): KotestConfiguration = config

   override fun passForkMode(forkMode: String?, tempFile: File?, parameters: JavaParameters?) {}

   override fun createJavaParameters(): JavaParameters {
      val params = super.createJavaParameters()
      // this main class is what will be executed by intellij when someone clicks run
      // it is a main function that will launch the KotestConsoleRunner
      params.mainClass = mainClass

      val packageName = configuration.getPackageName()
      if (packageName != null && packageName.isNotBlank())
         params.programParametersList.add("--package", packageName)

      // spec can be omitted if you want to run all tests in a module
      val specName = configuration.getSpecName()
      if (specName != null && specName.isNotBlank())
         params.programParametersList.add("--spec", specName)

      // test can be omitted if you want to run the entire spec or package
      val testPath = configuration.getTestPath()
      if (testPath != null && testPath.isNotBlank())
         params.programParametersList.add("--testpath", testPath)

      return params
   }

   override fun configureRTClasspath(javaParameters: JavaParameters, module: Module?) {
      try {
         requiredJars.forEach { javaParameters.classPath.addFirst(PathUtil.getJarPathForClass(Class.forName(it))) }
      } catch (e: Throwable) {
         println(e)
         e.printStackTrace()
         throw e
      }
   }

   override fun getScope(): TestSearchScope = TestSearchScope.WHOLE_PROJECT

   override fun passTempFile(parametersList: ParametersList, tempFilePath: String) {
      parametersList.add("-temp", tempFilePath)
   }

   override fun configureByModule(module: Module?): Boolean {
      return module != null
   }
}
