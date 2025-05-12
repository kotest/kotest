package io.kotest.matchers.compilation

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.ByteArrayOutputStream
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
private fun compileCodeSnippet(sourceFile: SourceFile): JvmCompilationResult {
   val kotlinCompilation = KotlinCompilation()
      .apply {
         sources = listOf(sourceFile)
         inheritClassPath = true
         verbose = false
         messageOutputStream = ByteArrayOutputStream()
         jvmTarget = Runtime.version().feature().toString()
      }
   val compilationResult = kotlinCompilation.compile()
   kotlinCompilation.workingDir.deleteRecursively()

   return compilationResult
}

@OptIn(ExperimentalCompilerApi::class)
private val compileMatcher = object : Matcher<SourceFile> {
   override fun test(sourceFile: SourceFile): MatcherResult {
      val compilationResult = compileCodeSnippet(sourceFile)
      return MatcherResult(
         compilationResult.exitCode == ExitCode.OK,
         { "Expected code to compile, but it failed to compile with error: \n${compilationResult.messages}" },
         { "Expected code to fail to compile, but it compile" }
      )
   }
}

@OptIn(ExperimentalCompilerApi::class)
private class DoesNotCompileMatcher(private val expectedMessage: String) : Matcher<SourceFile> {
   override fun test(value: SourceFile): MatcherResult {
      val compilationResult = compileCodeSnippet(value)
      val messages = compilationResult.messages
      val exitCode = compilationResult.exitCode

      val actuallyFailed = exitCode != ExitCode.OK
      val messageMatched = expectedMessage in messages

      return MatcherResult(
         actuallyFailed && messageMatched,
         {
            when {
               !actuallyFailed ->
                  "Expected code to fail to compile, but it compiled successfully."

               else -> buildString {
                  appendLine("Expected error message containing:")
                  appendLine(expectedMessage)
                  appendLine("But actual messages were:")
                  appendLine(messages)
               }
            }
         },
         {
            "Expected compilation to not fail with message containing: $expectedMessage, but it did."
         }
      )
   }
}

/**
 * Create a Kotlin code snippet with syntax highlighting
 */
fun codeSnippet(@Language("kotlin") sourceCode: String): SourceFile {
   return SourceFile.kotlin("KClass.kt", sourceCode)
}

/**
 * Assert that given codeSnippet [String] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available to the calling process are also available to the code snippet.
 * @see [String.shouldNotCompile]
 * */
fun String.shouldCompile() = codeSnippet(this).shouldCompile()

/**
 * Assert that given codeSnippet [String] does not compile successfully.
 *
 * If [expectedMessage] is provided, the test additionally verifies that the compilation fails
 * with an error message containing the specified text. This helps ensure that the compilation
 * fails for the expected reason, not due to an unrelated error.
 *
 * It includes the classpath of the calling process,
 * so that dependencies available to the calling process are also available to the code snippet.
 * @see [String.shouldCompile]
 * */
fun String.shouldNotCompile(expectedMessage: String? = null) = codeSnippet(this).shouldNotCompile(expectedMessage)

/**
 * Assert that given file [File] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available to the calling process are also available to the code snippet.
 * @see [File.shouldNotCompile]
 * */
fun File.shouldCompile() = codeSnippet(readText()).shouldCompile()

/**
 * Assert that given file [File] does not compile successfully.
 *
 * If [expectedMessage] is provided, the test additionally verifies that the compilation fails
 * with an error message containing the specified text. This helps ensure that the compilation
 * fails for the expected reason, not due to an unrelated error.
 *
 * It includes the classpath of the calling process,
 * so that dependencies available to the calling process are also available to the code snippet.
 * @see [File.shouldCompile]
 * */
fun File.shouldNotCompile(expectedMessage: String? = null) = codeSnippet(readText()).shouldNotCompile(expectedMessage)

/**
 * Assert that given codeSnippet [SourceFile] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available to the calling process are also available to the code snippet.
 * @see [SourceFile.shouldNotCompile]
 * */
fun SourceFile.shouldCompile() = this should compileMatcher

/**
 * Assert that given codeSnippet [SourceFile] does not compile successfully.
 *
 * If [expectedMessage] is provided, the test additionally verifies that the compilation fails
 * with an error message containing the specified text. This helps ensure that the compilation
 * fails for the expected reason, not due to an unrelated error.
 *
 * It includes the classpath of the calling process,
 * so that dependencies available to the calling process are also available to the code snippet.
 * @see [SourceFile.shouldCompile]
 * */
fun SourceFile.shouldNotCompile(expectedMessage: String? = null) {
   if (expectedMessage == null) {
      this shouldNot compileMatcher
   } else {
      this should DoesNotCompileMatcher(expectedMessage)
   }
}
