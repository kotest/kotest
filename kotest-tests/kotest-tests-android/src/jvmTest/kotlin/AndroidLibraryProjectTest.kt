package com.sksamuel.kotest.tests.android

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.io.File
import org.gradle.testkit.runner.GradleRunner

class AndroidLibraryProjectTest : FunSpec({

   val androidLibraryProjectDir = File(System.getProperty("androidLibraryProjectDir"))

   test("verify project runs tests") {
      val result = GradleRunner.create()
         .withProjectDir(androidLibraryProjectDir)
         .withArguments("test")
         .withEnvironment(
            mapOf(
               "ANDROID_HOME" to androidLibraryProjectDir.resolve("ANDROID_SDK").absolutePath,
            )
         )
         .build()

      result.output shouldContain "BUILD SUCCESSFUL"
   }

})
