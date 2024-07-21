package io.kotest.framework.multiplatform.gradle

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.file.shouldBeAFile
import io.kotest.matchers.string.shouldStartWith
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Why don't we use Gradle's TestKit here?
// It embeds a particular version of Kotlin, which causes all kinds of pain.
// See https://youtrack.jetbrains.com/issue/KT-24327 for one example.
class KotestMultiplatformCompilerGradlePluginSpec : ShouldSpec({
   setOf(
      "1.9.24",
      "2.0.0",
   ).forEach { kotlinVersion ->
      context("when the project targets Kotlin version $kotlinVersion") {
         val testProjectPath = Paths.get("test-project").toAbsolutePath()
         val testReportsDirectory = testProjectPath.resolve("build").resolve("test-results")

         beforeEach {
            if (Files.exists(testReportsDirectory)) {
               if (!testReportsDirectory.toFile().deleteRecursively()) {
                  throw RuntimeException("Could not delete test report directory $testReportsDirectory")
               }
            }
         }

         fun shouldHaveExpectedTestResultsFor(taskName: String) {
            withClue("$taskName test report") {
               val testReportFile = testReportsDirectory.resolve(taskName).resolve("TEST-TestSpec.xml")
               testReportFile.toFile().shouldBeAFile()

               val testReportContentBeginning =
                  Files.readAllBytes(testReportFile).decodeToString().lineSequence().take(2).joinToString("\n")

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
               "wasmJsNodeTest"
            )

            val invocation = GradleInvocation(
               testProjectPath,
               listOf(
                  "-PkotlinVersion=$kotlinVersion",
                  "-PuseNewNativeMemoryModel=false",
               ) + taskNames
            )

            val result = invocation.run()

            withClue(result.clue) {
               taskNames.forAll {
                  shouldHaveExpectedTestResultsFor(it)
               }
            }
         }

         setOf(
            true,
            false
         ).forEach { enableNewMemoryModel ->
            val description = if (enableNewMemoryModel) "is enabled" else "is not enabled"

            context("when the new Kotlin/Native memory model $description") {
               should("be able to compile and run tests for all native targets supported by the host machine") {
                  val taskNames = listOf(
                     "macosArm64Test",
                     "macosX64Test",
                     "mingwX64Test",
                     "linuxX64Test"
                  )

                  val invocation = GradleInvocation(
                     testProjectPath,
                     listOf(
                        "-PkotlinVersion=$kotlinVersion",
                        "-PuseNewNativeMemoryModel=$enableNewMemoryModel",
                     ) + taskNames
                  )

                  val result = invocation.run()

                  withClue(result.clue) {
                     taskNames.forAtLeastOne { taskName ->
                        // Depending on the host machine these tests are running on,
                        // only one of the test targets will be built and executed.
                        shouldHaveExpectedTestResultsFor(taskName)
                     }
                  }
               }
            }
         }
      }
   }
})

private data class GradleInvocation(
   val projectPath: Path,
   val arguments: List<String>,
) {
   val isWindows = "windows" in System.getProperty("os.name").orEmpty().lowercase()
   private val wrapperScriptName = if (isWindows) "gradlew.bat" else "gradlew"
   private val wrapperScriptPath: Path = Paths.get("..", "..", wrapperScriptName)

   class Result(command: List<String>, val output: String, val exitCode: Int) {
      val clue = "Gradle process $command exited with code $exitCode and output:\n" + output.prependIndent("\t>>> ")
   }

   fun run(): Result {
      val command = buildList {
         add(wrapperScriptPath.toAbsolutePath().toString())
         add("--continue")
         add("-PkotestGradlePluginVersion=$kotestGradlePluginVersion")
         add("-PkotestVersion=$kotestVersion")
         add("-PdevMavenRepoPath=$devMavenRepoPath")
         addAll(arguments)
      }

      val process = ProcessBuilder(command)
         .directory(projectPath.toFile())
         .redirectOutput(ProcessBuilder.Redirect.PIPE)
         .redirectError(ProcessBuilder.Redirect.PIPE)
         .redirectErrorStream(true)
         .start()

      return Result(
         command = command,
         output = InputStreamReader(process.inputStream).use { reader -> reader.readText() },
         exitCode = process.waitFor()
      )
   }

   companion object {
      private val kotestVersion = System.getProperty("kotestVersion")
      private val kotestGradlePluginVersion = System.getProperty("kotestGradlePluginVersion")
      private val devMavenRepoPath = System.getProperty("devMavenRepoPath")
   }
}
