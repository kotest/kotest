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
private val compiles = object : Matcher<SourceFile> {
   override fun test(sourceFile: SourceFile): MatcherResult {
      val compilationResult = compileCodeSnippet(sourceFile)
      return MatcherResult(
         compilationResult.exitCode == ExitCode.OK,
         { "Expected code to compile, but it failed to compile with error: \n${compilationResult.messages}" },
         { "Expected code to fail to compile, but it compile" }
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
 * so that dependencies available for calling process will be available for code snippet as well.
 * @see [String.shouldNotCompile]
 * */
fun String.shouldCompile() = codeSnippet(this).shouldCompile()

/**
 * Assert that given codeSnippet [String] does not compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet as well.
 * @see [String.shouldCompile]
 * */
fun String.shouldNotCompile() = codeSnippet(this).shouldNotCompile()

/**
 * Assert that given file [File] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet as well.
 * @see [File.shouldNotCompile]
 * */
fun File.shouldCompile() = codeSnippet(readText()).shouldCompile()

/**
 * Assert that given file [File] does not compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet as well.
 * @see [File.shouldCompile]
 * */
fun File.shouldNotCompile() = codeSnippet(readText()).shouldNotCompile()

/**
 * Assert that given codeSnippet [SourceFile] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet as well.
 * @see [SourceFile.shouldNotCompile]
 * */
fun SourceFile.shouldCompile() = this should compiles

/**
 * Assert that given codeSnippet [SourceFile] does not compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet as well.
 * @see [SourceFile.shouldCompile]
 * */
fun SourceFile.shouldNotCompile() = this shouldNot compiles
