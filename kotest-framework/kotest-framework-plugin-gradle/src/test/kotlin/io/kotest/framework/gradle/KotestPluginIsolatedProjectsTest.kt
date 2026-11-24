package io.kotest.framework.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files

class KotestPluginIsolatedProjectsTest : FunSpec({

   // a multi-project build with Gradle isolated projects enabled, the plugin is applied by the :app subproject
   fun sample(): File {
      val projectDir = Files.createTempDirectory("kotest-isolated-projects").toFile()
      projectDir.resolve("settings.gradle.kts").writeText(
         """
         rootProject.name = "sample"
         include("app")
         """.trimIndent()
      )
      projectDir.resolve("gradle.properties").writeText("org.gradle.unsafe.isolated-projects=true\n")
      projectDir.resolve("app").mkdirs()
      // the last block prints what the plugin configured, so that the tests can assert on it
      projectDir.resolve("app/build.gradle.kts").writeText(
         """
         plugins {
            kotlin("jvm")
            id("io.kotest")
         }

         afterEvaluate {
            val task = tasks.named<io.kotest.framework.gradle.tasks.KotestJvmTask>("jvmKotest").get()
            println("$INCLUDE_PREFIX" + task.include.orNull)
            println("$ROOT_REPORTS_PREFIX" + task.rootTestReportsDir.get().asFile.canonicalPath)
         }
         """.trimIndent()
      )
      return projectDir
   }

   // listing the tasks configures every task, including the ones the plugin registers lazily
   fun configure(projectDir: File, vararg arguments: String): BuildResult = GradleRunner.create()
      .withProjectDir(projectDir)
      .withPluginClasspath()
      .withArguments(*arguments, ":app:tasks", "--all")
      .build()

   // see https://github.com/kotest/kotest/issues/6297
   test("the plugin is compatible with Gradle isolated projects") {
      val result = configure(sample())

      // isolated projects turn the configuration cache on, so fail loudly if Gradle ignores the property
      result.output shouldContain "Configuration cache entry stored."
   }

   // the plugin cannot read the build directory of the root project with isolated projects,
   // so the root reports always go to the default build directory of the root project
   test("root reports stay under <rootDir>/build when the root project relocates its build directory") {
      val projectDir = sample()
      projectDir.resolve("build.gradle.kts").writeText(
         """
         layout.buildDirectory.set(layout.projectDirectory.dir("relocated"))
         """.trimIndent()
      )

      val result = configure(projectDir)

      val expectedRootReports = File(projectDir, "build").resolve("test-results/jvmKotest").canonicalPath
      result.output.lineSequence().filter { it.startsWith(ROOT_REPORTS_PREFIX) }.toList() shouldBe listOf(
         "$ROOT_REPORTS_PREFIX$expectedRootReports"
      )
   }

   test("kotest.include passed as a Gradle property is honoured with isolated projects") {
      val include = "com.example.SampleSpec"

      val result = configure(sample(), "-P${KotestPlugin.KOTEST_INCLUDE_PROPERTY}=$include")

      result.output.lineSequence().filter { it.startsWith(INCLUDE_PREFIX) }.toList() shouldBe listOf(
         "$INCLUDE_PREFIX$include"
      )
   }
}) {
   companion object {
      private const val INCLUDE_PREFIX = "kotestTaskInclude="
      private const val ROOT_REPORTS_PREFIX = "kotestTaskRootReports="
   }
}
