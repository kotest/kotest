package io.kotest.framework.multiplatform.gradle

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestScope
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.paths.shouldBeAFile
import io.kotest.matchers.string.shouldStartWith
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

class KotestMultiplatformCompilerGradlePluginSpec : ShouldSpec({
   setOf(
      "1.9.24",
      "2.0.0",
   ).forEach { kotlinVersion ->
      context("when the project targets Kotlin version $kotlinVersion") {

         fun GradleInvocation.Result.shouldHaveExpectedTestResultsFor(taskName: String) {
            withClue("$taskName test report") {
               val testReportFile = testReportsDirectory.resolve("$taskName/TEST-TestSpec.xml")
               testReportFile.shouldBeAFile()

               val testReportContentBeginning = testReportFile.useLines { it.take(2).joinToString("\n") }

               testReportContentBeginning.shouldStartWith(
                  """
                  <?xml version="1.0" encoding="UTF-8"?>
                  <testsuite name="TestSpec" tests="3" skipped="0" failures="1" errors="0"
                  """.trimIndent()
               )
            }
         }

         should("be able to compile and run tests for the JVM, JS and Wasm/JS targets") {
            val taskNames = listOf(
               "jvmTest",
               "jsBrowserTest",
               "jsNodeTest",
               "wasmJsBrowserTest",
               "wasmJsNodeTest",
            )

            runGradle(
               kotlinVersion = kotlinVersion,
               taskNames = taskNames,
            ) { result ->
               taskNames.forAll { taskName ->
                  result.shouldHaveExpectedTestResultsFor(taskName)
               }
            }
         }

         should("be able to compile and run tests for all native targets supported by the host machine") {
            val taskNames = listOf(
               "macosArm64Test",
               "macosX64Test",
               "mingwX64Test",
               "linuxX64Test",
            )

            runGradle(
               kotlinVersion = kotlinVersion,
               taskNames = taskNames,
            ) { result ->
               taskNames.forAtLeastOne { taskName ->
                  // Depending on the host machine these tests are running on,
                  // only one of the test targets will be built and executed.
                  result.shouldHaveExpectedTestResultsFor(taskName)
               }
            }
         }
      }
   }
})

private fun TestScope.runGradle(
   kotlinVersion: String,
   taskNames: List<String>,
   block: (result: GradleInvocation.Result) -> Unit,
) {
   GradleInvocation(
      kotlinVersion = kotlinVersion,
      taskNames = taskNames,
      testId = testCase.descriptor.id.value,
   ).use { gradle ->
      val result = gradle.run()
      println("[${testCase.name.testName}] result log ${result.output.absolute()}")
      withClue({ result.clue() }) {
         block(result)
      }
   }
}

private data class GradleInvocation(
   val kotlinVersion: String,
   val taskNames: List<String>,
   val testId: String,
) : AutoCloseable {
   val projectDir = createTempDirectory("kotest-gradle-plugin-test")

   data class Result(
//      val command: List<String>,
      val output: Path,
//      val exitCode: Int,
      val projectDir: Path,
   ) {
      val testReportsDirectory: Path = projectDir.resolve("build/test-results")

      fun clue(): String =
         output.readText().prependIndent("\t>>> ")
//         "Gradle process $command exited with code $exitCode and output:\n" + output.readText().prependIndent("\t>>> ")
   }

   fun run(): Result {
      prepareProjectDir(projectDir)

      val logFile = createTempFile(testLogDir, testId.replaceNonAlphanumeric(), ".log")

      val result = logFile.bufferedWriter().use { logWriter ->
         @Suppress("UnstableApiUsage")
         GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .forwardStdOutput(logWriter)
            .withEnvironment(
               mapOf(
                  "PATH" to System.getenv("PATH"),
                  "GRADLE_USER_HOME" to gradleUserHome.toString(),
                  "GRADLE_RO_DEP_CACHE" to hostGradleUserHome.resolve("caches").toString(),
               )
            )
            .withArguments(
               buildList {
                  add("--continue")
                  addAll(taskNames)
               }
            )
            .run()
      }

//      val command = buildList {
//         add(wrapperScriptPath.toString())
//      }

//      val process = ProcessBuilder(command)
//         .directory(projectDir.toFile())
//         .redirectOutput(logFile.toFile())
//         .redirectError(logFile.toFile())
//         .redirectErrorStream(true)
//         .apply {
//            environment().apply {
//            }
//         }
//         .start()

      return Result(
//         command = command,
         output = logFile,
//         exitCode = process.waitFor(),
         projectDir = projectDir,
      )
   }

   private fun prepareProjectDir(projectDir: Path): Path {
      val excludedDirs = setOf(
         ".kotlin",
         "build",
         ".gradle",
         ".idea",
         "kotlin-js-store",
      )

      testProjectDir.copyToRecursively(
         target = projectDir,
         followLinks = false,
      ) { src, target ->
         if (src.isDirectory() && src.name in excludedDirs) {
            CopyActionResult.SKIP_SUBTREE
         } else {
            src.copyToIgnoringExistingDirectory(target, followLinks = false)
         }
      }

      projectDir.resolve("gradle.properties").apply {
         writeText(
            buildString {
               appendLine(readText())
               appendLine("kotlinVersion=$kotlinVersion")
               appendLine("kotestVersion=$kotestVersion")
               appendLine("devMavenRepoPath=$devMavenRepoPath")
            }
         )
      }

      return projectDir
   }

   override fun close() {
//      projectDir.deleteRecursively()
   }

   companion object {

      /** Access the current host's Gradle user dir, to use as a read-only cache. */
      private val hostGradleUserHome = Path(System.getProperty("gradleUserHomeDir"))

      private val testLogDir = Path(System.getProperty("testLogDir"))
         .resolve(System.currentTimeMillis().toString())
         .createDirectories()

      /** Use a stable Gradle user home for each test. */
      private val gradleUserHome = Files.createTempDirectory("test-gradle-user-home")

      private val kotestVersion = System.getProperty("kotestVersion")
      private val devMavenRepoPath = System.getProperty("devMavenRepoPath")

      /** The source project that will be tested. This directory should not be modified. */
      private val testProjectDir = Path(System.getProperty("testProjectDir"))

      private fun String.replaceNonAlphanumeric(
         replacement: String = "-"
      ): String =
         map { if (it.isLetterOrDigit()) it else replacement }.joinToString("")
   }
}
