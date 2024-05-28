package io.kotest.framework.multiplatform.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class JsTask : DefaultTask() {

   private var tests: String? = null

   @Option(option = "tests", description = "Specifies a subset of tests to execute")
   fun setTests(tests: String) {
      this.tests = tests
   }

   @TaskAction
   fun run() {

      val testModule = "${project.name}-test"
      val entryPackage = "io.kotest.js"
      val runFnName = "runKotest"

      project.exec {
         val buildDir = project.layout.buildDirectory.asFile.get().toPath()
         val moduleFile = buildDir.resolve("js/packages/${testModule}/kotlin/${testModule}.js")
         val testFilter = if (tests == null) null else "'$tests'"
         val runKotestCommand = "require('${moduleFile}').$entryPackage.$runFnName($testFilter)"
         commandLine("node", "-e", runKotestCommand)
      }
   }
}
