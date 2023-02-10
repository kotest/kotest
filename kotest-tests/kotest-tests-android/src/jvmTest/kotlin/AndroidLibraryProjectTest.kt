package com.sksamuel.kotest.tests.android

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.io.File
import org.gradle.testkit.runner.GradleRunner

class AndroidLibraryProjectTest : FunSpec({

   val androidLibraryProjectDir = File(
      System.getProperty("androidLibraryProjectDir") ?: error("missing androidLibraryProjectDir system property")
   )

   val runner = GradleRunner.create()
      .withProjectDir(androidLibraryProjectDir)
      .withEnvironment(
         mapOf(
            "ANDROID_HOME" to androidLibraryProjectDir.resolve("ANDROID_SDK").absolutePath,
         )
      )
      .forwardOutput()

   test("verify project runs tests") {
      val result = runner.withArguments(
         "clean",
         "check",

         "--info",
         "--stacktrace",

         // disable all the caching
         "--no-build-cache",
         "--no-configuration-cache",
         "--rerun-tasks",
      ).build()

      result.output shouldContain "BUILD SUCCESSFUL"
   }

   test("verify no duplicate classes") {
      val result = runner.withArguments(
         "clean",

         // this task triggers the 'duplicate classes' test
         "checkDebugAndroidTestDuplicateClasses",

         "--info",
         "--stacktrace",

         // disable all the caching
         "--no-build-cache",
         "--no-configuration-cache",
         "--rerun-tasks",
      ).build()

      result.output shouldContain "BUILD SUCCESSFUL"
   }
})
