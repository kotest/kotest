package io.kotest.framework.multiplatform.gradle

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePluginSpec.Companion.gradleWrapperPath
import io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePluginSpec.Companion.testProjectPath
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.file.shouldBeAFile
import io.kotest.matchers.string.shouldContainInOrder
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


// Why don't we use Gradle's TestKit here?
// It embeds a particular version of Kotlin, which causes all kinds of pain.
// See https://youtrack.jetbrains.com/issue/KT-24327 for one example.
class KotestMultiplatformCompilerGradlePluginSpec : ShouldSpec({
   val kotestVersion = System.getProperty("kotestVersion")

   val kotlinVersions = setOf(
      "1.6.21",
      "1.7.0",
   )

   beforeAny {
      // Build the test project ahead of time so that it doesn't try to build the Kotest libraries during
      // the tests. (if it tries to build them during the tests this can lead to conflicts).
      kotlinVersions.forEach { kotlinVersion ->
         GradleInvocation(
            "-PkotlinVersion=$kotlinVersion",
            "-PkotestVersion=$kotestVersion",
            "-PuseNewNativeMemoryModel=false",
            "build",
            "-x", "check",
         ).run()
      }
   }

   beforeEach {
      if (Files.exists(testReportsDirectory)) {
         if (!testReportsDirectory.toFile().deleteRecursively()) {
            throw RuntimeException("Could not delete test report directory $testReportsDirectory")
         }
      }
   }

   kotlinVersions.forEach { kotlinVersion ->
      context("when the project targets Kotlin version $kotlinVersion") {

         fun shouldHavePassingTestResultsFor(taskName: String) {
            val testReportFile = testReportsDirectory.resolve(taskName).resolve("TEST-TestSpec.xml")
            testReportFile.toFile().shouldBeAFile()

            val testReportContents: String = Files.readAllBytes(testReportFile).decodeToString()

            testReportContents.shouldContainInOrder(
               """  <?xml version="1.0" encoding="UTF-8"?>                                    """.trim(),
               """  <testsuite name="TestSpec" tests="2" skipped="0" failures="0" errors="0"  """.trim(),
            )
         }

         should("be able to compile and run tests for the JVM and JS targets") {
            val invocation = GradleInvocation(
               "-PkotlinVersion=$kotlinVersion",
               "-PkotestVersion=$kotestVersion",
               "-PuseNewNativeMemoryModel=false",
               "jvmTest",
               // FIXME: re-enable this once the issue described in https://github.com/kotest/kotest/pull/3107#issue-1301849119 is fixed
               // "jsBrowserTest",
               "jsNodeTest",
            )

            invocation.run()

            shouldHavePassingTestResultsFor("jvmTest")
            // FIXME: re-enable this once the issue described in https://github.com/kotest/kotest/pull/3107#issue-1301849119 is fixed
            // shouldHavePassingTestResultsFor("jsBrowserTest")
            shouldHavePassingTestResultsFor("jsNodeTest")
         }

         setOf(
            true,
            false
         ).forEach { enableNewMemoryModel ->
            val description = if (enableNewMemoryModel) "is enabled" else "is not enabled"

            context("when the new Kotlin/Native memory model $description") {
               should("be able to compile and run tests for all native targets supported by the host machine") {
                  val invocation = GradleInvocation(
                     "-PkotlinVersion=$kotlinVersion",
                     "-PkotestVersion=$kotestVersion",
                     "-PuseNewNativeMemoryModel=$enableNewMemoryModel",
                     "macosArm64Test",
                     "macosX64Test",
                     "mingwX64Test",
                     "linuxX64Test"
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
}) {
   companion object {
      val testProjectPath: Path = Paths.get("test-project").toAbsolutePath()
      val testReportsDirectory: Path = testProjectPath.resolve("build").resolve("test-results")
      // set in build.gradle.kts
      val gradleWrapperPath: String = System.getProperty("gradleWrapper")
   }
}

data class GradleInvocation(
   val projectPath: Path = testProjectPath,
   val arguments: List<String>,
) {
   private val wrapperScriptPath: Path = Paths.get(gradleWrapperPath)

   constructor(
      vararg arguments: String,
   ) : this(arguments = arguments.toList())

   fun run() {
      val command = listOf(
         wrapperScriptPath.toAbsolutePath().toString(),
         "--console=plain",
         "--no-daemon",
         "--stacktrace",
      ) + arguments

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
