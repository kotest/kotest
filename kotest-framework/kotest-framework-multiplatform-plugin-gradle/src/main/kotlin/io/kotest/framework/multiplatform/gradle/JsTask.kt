package io.kotest.framework.multiplatform.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class JsTask : DefaultTask() {

   private var testpath: String? = null

   @Option(option = "testpath", description = "Specifies a subset of tests to execute")
   fun setTestpath(testpath: String) {
      this.testpath = testpath
   }

   @TaskAction
   fun run() {

      val testModule = "${project.name}-test"
      val entryPackage = "io.kotest.js"
      val runFnName = "runKotest"

      project.exec {
         val buildDir = project.layout.buildDirectory.asFile.get().toPath()
         val moduleFile = buildDir.resolve("js/packages/${testModule}/kotlin/${testModule}.js")
         val testFilter = if (testpath == null) null else "'$testpath'"
         val runKotestCommand = "require('${moduleFile}').$entryPackage.$runFnName($testFilter)"
         commandLine("node", "-e", runKotestCommand)
      }
   }
}
