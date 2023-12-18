package io.kotest.framework.multiplatform.gradle

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.file.shouldBeAFile
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.shouldBe
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Why don't we use Gradle's TestKit here?
// It embeds a particular version of Kotlin, which causes all kinds of pain.
// See https://youtrack.jetbrains.com/issue/KT-24327 for one example.
class KotestMultiplatformCompilerGradlePluginSpec : ShouldSpec({
   val kotestVersion = System.getProperty("kotestVersion")

   setOf(
      "1.9.21",
//      "2.0.0-Beta2",
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
            val testReportFile = testReportsDirectory.resolve(taskName).resolve("TEST-TestSpec.xml")
            testReportFile.toFile().shouldBeAFile()

            val testReportContents = Files.readAllBytes(testReportFile).decodeToString()

            withClue("$taskName test report") {
               // FIXME: java.lang.NoClassDefFoundError: io/kotest/matchers/string/StartKt
               //      occurs with
               //          testReportContents shouldStartWith """
               //      when running `gradlew :kotest-framework:kotest-framework-multiplatform-plugin-gradle:test`
               //      on some platform locally (but works on CI)
               testReportContents.startsWith(
                  """
                  <?xml version="1.0" encoding="UTF-8"?>
                  <testsuite name="TestSpec" tests="3" skipped="0" failures="1" errors="0"
                  """.trimIndent()
               ) shouldBe true
            }
         }

         should("be able to compile and run tests for the JVM, JS and Wasm/JS targets") {
            val taskNames = listOf(
               "jvmTest",
               "jsBrowserTest",
               // "jsNodeTest", // FIXME: Enable when #3329 "Node JS tests do not report failures correctly" is resolved
               "wasmJsBrowserTest",
               "wasmJsNodeTest"
            )

            val invocation = GradleInvocation(
               testProjectPath,
               listOf(
                  "-PkotlinVersion=$kotlinVersion",
                  "-PkotestVersion=$kotestVersion",
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
                        "-PkotestVersion=$kotestVersion",
                        "-PuseNewNativeMemoryModel=$enableNewMemoryModel"
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
   val arguments: List<String>
) {
   private val wrapperScriptPath: Path = Paths.get("..", "..", "gradlew")

   class Result(command: List<String>, val output: String, val exitCode: Int) {
      val clue = "Gradle process $command exited with code $exitCode and output:\n" + output.prependIndent("\t>>> ")
   }

   fun run(): Result {
      val command =
         listOf(wrapperScriptPath.toAbsolutePath().toString(), "--console=plain", "--no-daemon", "--continue") +
            arguments

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
}
