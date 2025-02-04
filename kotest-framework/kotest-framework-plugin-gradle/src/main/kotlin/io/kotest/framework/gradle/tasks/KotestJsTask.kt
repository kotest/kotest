package io.kotest.framework.gradle.tasks

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestJsTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   @TaskAction
   protected fun execute() {

      val testModule = "${project.name}-test"
      val entryPackage = "io.kotest.js"
      val runFnName = "runKotest"

      executors.exec {
         val buildDir = project.layout.buildDirectory.asFile.get().toPath()
         val moduleFile = buildDir.resolve("js/packages/${testModule}/kotlin/${testModule}.js")
         val testFilter = if (tests.orNull == null) null else "'$tests'"
         val runKotestCommand = "require('${moduleFile}').$entryPackage.$runFnName($testFilter)"
         commandLine("node", "-e", runKotestCommand)
      }
   }
}
