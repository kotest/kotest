package io.kotest.framework.multiplatform.gradle

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

class KotestPluginExtensionSpec : ShouldSpec({

   context("kts Gradle project") {

      should("be able to build") {
         val testDir = tempdir()

         val settingsGradleKts = testDir.resolve("settings.gradle.kts")
         settingsGradleKts.writeText(
            """
               rootProject.name = "kotest-plugin-extension-test"
            """.trimIndent()
         )

         val buildGradleKts = testDir.resolve("build.gradle.kts")
         buildGradleKts.writeText(
            """
               plugins {
                 id("io.kotest.multiplatform")
               }

               kotest {
                 kotestCompilerVersion.set("1.2.3")
               }
            """.trimIndent()
         )

         val result = GradleRunner.create()
            .withProjectDir(testDir)
            .withArguments("build")
            .build()

         result.task("build")?.outcome shouldBe TaskOutcome.SUCCESS
      }
   }

}) {
   companion object {

   }
}
