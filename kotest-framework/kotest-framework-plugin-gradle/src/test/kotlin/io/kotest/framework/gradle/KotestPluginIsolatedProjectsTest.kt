package io.kotest.framework.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files

class KotestPluginIsolatedProjectsTest : FunSpec({

   // see https://github.com/kotest/kotest/issues/6297
   test("the plugin is compatible with Gradle isolated projects") {
      val projectDir = Files.createTempDirectory("kotest-isolated-projects").toFile()
      projectDir.resolve("settings.gradle.kts").writeText(
         """
         rootProject.name = "sample"
         include("app")
         """.trimIndent()
      )
      projectDir.resolve("gradle.properties").writeText("org.gradle.unsafe.isolated-projects=true\n")
      projectDir.resolve("app").mkdirs()
      projectDir.resolve("app/build.gradle.kts").writeText(
         """
         plugins {
            kotlin("jvm")
            id("io.kotest")
         }
         """.trimIndent()
      )

      // listing the tasks configures every task, including the ones the plugin registers lazily
      val result = GradleRunner.create()
         .withProjectDir(projectDir)
         .withPluginClasspath()
         .withArguments(":app:tasks", "--all")
         .build()

      // isolated projects turn the configuration cache on, so fail loudly if Gradle ignores the property
      result.output shouldContain "Configuration cache entry stored."
   }
})
