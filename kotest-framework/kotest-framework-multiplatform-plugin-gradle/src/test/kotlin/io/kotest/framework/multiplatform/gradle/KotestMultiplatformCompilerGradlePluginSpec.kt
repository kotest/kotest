package io.kotest.framework.multiplatform.gradle

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.file.shouldBeAFile
import io.kotest.matchers.string.shouldStartWith
import io.kotest.inspectors.forAtLeastOne
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

         fun shouldHavePassingTestResultsFor(taskName: String) {
            val testReportFile = testReportsDirectory.resolve(taskName).resolve("TEST-TestSpec.xml")
            testReportFile.toFile().shouldBeAFile()

            val testReportContents = Files.readAllBytes(testReportFile).decodeToString()

            testReportContents shouldStartWith """
               <?xml version="1.0" encoding="UTF-8"?>
               <testsuite name="TestSpec" tests="2" skipped="0" failures="0" errors="0"
            """.trimIndent()
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
                  "-PkotestVersion=$kotestVersion",
                  "-PuseNewNativeMemoryModel=false",
               ) + taskNames
            )

            invocation.run()

            taskNames.forAll {
               shouldHavePassingTestResultsFor(it)
            }
         }

         setOf(
            true,
            false
         ).forEach { enableNewMemoryModel ->
            val description = if (enableNewMemoryModel) "is enabled" else "is not enabled"

            context("when the new Kotlin/Native memory model $description") {
               should("be able to compile and run tests for all native targets supported by the host machine") {
                  val invocation = GradleInvocation(
                     testProjectPath,
                     listOf(
                        "-PkotlinVersion=$kotlinVersion",
                        "-PkotestVersion=$kotestVersion",
                        "-PuseNewNativeMemoryModel=$enableNewMemoryModel",
                        "macosArm64Test",
                        "macosX64Test",
                        "mingwX64Test",
                        "linuxX64Test"
                     )
                  )

                  invocation.run()

                  setOf(
                     "mingwX64Test",
                     "macosX64Test",
                     "macosArm64Test",
                     "linuxX64Test"
                  ).forAtLeastOne { taskName ->
                     // Depending on the host machine these tests are running on, only one of the test targets will be built and executed.
                     shouldHavePassingTestResultsFor(taskName)
                  }
               }
            }
         }
      }
   }
})

data class GradleInvocation(
   val projectPath: Path,
   val arguments: List<String>
) {
   private val wrapperScriptPath: Path = Paths.get("..", "..", "gradlew")

   fun run() {
      val command = listOf(wrapperScriptPath.toAbsolutePath().toString(), "--console=plain", "--no-daemon") + arguments

      val process = ProcessBuilder(command)
         .directory(projectPath.toFile())
         .redirectOutput(ProcessBuilder.Redirect.PIPE)
         .redirectError(ProcessBuilder.Redirect.PIPE)
         .redirectErrorStream(true)
         .start()

      val output = InputStreamReader(process.inputStream).use { reader -> reader.readText() }
      val exitCode = process.waitFor()

      if (exitCode != 0) {
         throw RuntimeException("Gradle process $command exited with code $exitCode and output:\n$output")
      }
   }
}
