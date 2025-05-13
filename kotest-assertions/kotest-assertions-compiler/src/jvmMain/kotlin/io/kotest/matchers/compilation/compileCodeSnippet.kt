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

/**
 * Represents a configuration wrapper for Kotlin compilation used in testing,
 * allowing to customize jvmTarget, classpath, compiler plugins, symbol processors or other compiler options.
 *
 * The configuration is used as a template and applied to each instance of [CodeSnippet].
 *
 * **Note:** You do **not** need to specify [KotlinCompilation.sources] inside the [configure] block.
 * It will be overridden when compiling a [CodeSnippet], which provides its own source file.
 */
@OptIn(ExperimentalCompilerApi::class)
class CompileConfig(private val configure: KotlinCompilation.() -> Unit) {
   internal val compilationFactory: () -> KotlinCompilation = {
      val kotlinCompilation = KotlinCompilation()
         .apply {
            inheritClassPath = true
            verbose = false
            messageOutputStream = ByteArrayOutputStream()
            jvmTarget = Runtime.version().feature().toString()
         }
      kotlinCompilation.configure()
      kotlinCompilation
   }
}

/**
 * Represents a Kotlin code snippet to be compiled in tests.
 */
interface CodeSnippet {
   /**
    * Compiles this code snippet.
    *
    * This method should generally **not** be used directly in test code. Prefer using higher-level
    * assertion functions such as [CodeSnippet.shouldCompile] and [CodeSnippet.shouldNotCompile],
    * which provides clear failure messages and better integration with Kotest.
    *
    * Use [compile] directly only if you need to define your own assertions using the detailed
    * [JvmCompilationResult], such as checking specific diagnostic messages or generated files.
    */
   @OptIn(ExperimentalCompilerApi::class)
   fun compile(): JvmCompilationResult
}

/**
 * Compiles this [CodeSnippet] and applies the given [block] to the resulting [JvmCompilationResult].
 *
 * This is a convenient way to perform assertions or inspections on the compilation result
 * without manually handling the result variable.
 *
 * Intended for use in advanced test scenarios where direct access to compilation diagnostics,
 * generated files, or exit codes is needed.
 *
 * Prefer using [shouldCompile] and [shouldNotCompile] for common assertions.
 *
 * @param block A lambda that receives the [JvmCompilationResult] and returns a value.
 * @return The result of applying [block] to the compilation result.
 */
@OptIn(ExperimentalCompilerApi::class)
fun <T> CodeSnippet.compile(block: JvmCompilationResult.() -> T): T {
   return compile().block()
}

private class CodeSnippetImpl(val sourceFile: SourceFile, val compileConfig: CompileConfig): CodeSnippet {
   @OptIn(ExperimentalCompilerApi::class)
   override fun compile(): JvmCompilationResult {
      val kotlinCompilation = compileConfig.compilationFactory()
      kotlinCompilation.sources = listOf(sourceFile)
      val compilationResult = kotlinCompilation.compile()
      kotlinCompilation.workingDir.deleteRecursively()
      return compilationResult
   }
}

@OptIn(ExperimentalCompilerApi::class)
private val compileMatcher = object : Matcher<CodeSnippet> {
   override fun test(value: CodeSnippet): MatcherResult {
      val compilationResult = value.compile()
      return MatcherResult(
         compilationResult.exitCode == ExitCode.OK,
         { "Expected code to compile, but it failed to compile with error: \n${compilationResult.messages}" },
         { "Expected code to fail to compile, but it compile" }
      )
   }
}

@OptIn(ExperimentalCompilerApi::class)
private class DoesNotCompileMatcher(private val expectedMessage: String) : Matcher<CodeSnippet> {
   override fun test(value: CodeSnippet): MatcherResult {
      val compilationResult = value.compile()
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

@OptIn(ExperimentalCompilerApi::class)
private val defaultCompileConfig = CompileConfig {}

/**
 * Creates a [CodeSnippet] from a raw Kotlin source string with syntax highlighting.
 *
 * The resulting snippet will be compiled using the current [CompileConfig], which allows
 * customization of the compilation environment, such as enabling compiler plugins.
 */
@OptIn(ExperimentalCompilerApi::class)
fun CompileConfig.codeSnippet(@Language("kotlin") sourceCode: String): CodeSnippet {
   return CodeSnippetImpl(SourceFile.kotlin("KClass.kt", sourceCode), this)
}

/**
 * Creates a [CodeSnippet] from a raw Kotlin source string with syntax highlighting.
 */
fun codeSnippet(@Language("kotlin") sourceCode: String): CodeSnippet {
   return defaultCompileConfig.codeSnippet(sourceCode)
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
 * Assert that given [File] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available to the calling process are also available to the code snippet.
 * @see [File.shouldNotCompile]
 * */
fun File.shouldCompile() = codeSnippet(readText()).shouldCompile()

/**
 * Assert that given [File] does not compile successfully.
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
 * Assert that given [CodeSnippet] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available to the calling process are also available to the code snippet.
 * @see [CodeSnippet.shouldNotCompile]
 * */
fun CodeSnippet.shouldCompile() = this should compileMatcher

/**
 * Assert that given [CodeSnippet] does not compile successfully.
 *
 * If [expectedMessage] is provided, the test additionally verifies that the compilation fails
 * with an error message containing the specified text. This helps ensure that the compilation
 * fails for the expected reason, not due to an unrelated error.
 *
 * It includes the classpath of the calling process,
 * so that dependencies available to the calling process are also available to the code snippet.
 * @see [CodeSnippet.shouldCompile]
 * */
fun CodeSnippet.shouldNotCompile(expectedMessage: String? = null) {
   if (expectedMessage == null) {
      this shouldNot compileMatcher
   } else {
      this should DoesNotCompileMatcher(expectedMessage)
   }
}
