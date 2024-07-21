@file:OptIn(ExperimentalPathApi::class)

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
import kotlin.io.path.CopyActionResult
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText

// Why don't we use Gradle's TestKit here?
// It embeds a particular version of Kotlin, which causes all kinds of pain.
// See https://youtrack.jetbrains.com/issue/KT-24327 for one example.
class KotestMultiplatformCompilerGradlePluginSpec : ShouldSpec({
   setOf(
      "1.9.24",
      "2.0.0",
   ).forEach { kotlinVersion ->
      context("when the project targets Kotlin version $kotlinVersion") {

         fun GradleInvocation.Result.shouldHaveExpectedTestResultsFor(taskName: String) {
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

            runGradle(
               listOf(
                  "-PkotlinVersion=$kotlinVersion",
                  "-PuseNewNativeMemoryModel=false",
               ) + taskNames
            ) { result ->
               taskNames.forAll {
                  result.shouldHaveExpectedTestResultsFor(it)
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

                  runGradle(
                     listOf(
                        "-PkotlinVersion=$kotlinVersion",
                        "-PuseNewNativeMemoryModel=$enableNewMemoryModel",
                     ) + taskNames
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
      }
   }
})

private fun runGradle(
   arguments: List<String>,
   block: (result: GradleInvocation.Result) -> Unit,
) {
   GradleInvocation(arguments).use {
      val result = it.run()
      withClue(result.clue) {
         block(result)
      }
   }
}

private data class GradleInvocation(
   val arguments: List<String>,
) : AutoCloseable {
   val projectDir = createTempDirectory("kotest-gradle-plugin-test")

   data class Result(
      val command: List<String>,
      val output: String,
      val exitCode: Int,
      val projectDir: Path,
   ) {
      val testReportsDirectory: Path = projectDir.resolve("build/test-results")

      val clue = "Gradle process $command exited with code $exitCode and output:\n" + output.prependIndent("\t>>> ")
   }

   fun run(): Result {
      prepareProjectDir(projectDir)

      val command = buildList {
         add(wrapperScriptPath.toString())
         add("--continue")
         add("-PkotestGradlePluginVersion=$kotestGradlePluginVersion")
         add("-PkotestVersion=$kotestVersion")
         add("-PdevMavenRepoPath=$devMavenRepoPath")
         addAll(arguments)
      }

      val process = ProcessBuilder(command)
         .directory(projectDir.toFile())
         .redirectOutput(ProcessBuilder.Redirect.PIPE)
         .redirectError(ProcessBuilder.Redirect.PIPE)
         .redirectErrorStream(true)
         .start()

      return Result(
         command = command,
         output = InputStreamReader(process.inputStream).use { reader -> reader.readText() },
         exitCode = process.waitFor(),
         projectDir = projectDir,
      )
   }

   override fun close() {
      projectDir.deleteRecursively()
   }

   companion object {
      private val kotestVersion = System.getProperty("kotestVersion")
      private val kotestGradlePluginVersion = System.getProperty("kotestGradlePluginVersion")
      private val devMavenRepoPath = System.getProperty("devMavenRepoPath")

      private val kotestProjectDir = Path("../../").normalize().absolute()

      private val testProjectDir = Path(System.getProperty("testProjectDir"))

      private val wrapperScriptPath: Path = run {
         val isWindows = "windows" in System.getProperty("os.name").orEmpty().lowercase()
         val wrapperScriptName = if (isWindows) "gradlew.bat" else "gradlew"
         Paths.get("..", "..", wrapperScriptName).normalize().absolute()
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

         projectDir.resolve("settings.gradle.kts").apply {
            writeText(
               readText().replace(
                  """includeBuild("../../../")""",
                  """includeBuild("${kotestProjectDir.invariantSeparatorsPathString}")""",
               )
            )
         }

         return projectDir
      }
   }
}
