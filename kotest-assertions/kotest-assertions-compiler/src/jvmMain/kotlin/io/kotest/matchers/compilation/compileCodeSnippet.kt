package io.kotest.matchers.compilation

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.ByteArrayOutputStream
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
private fun compileCodeSnippet(codeSnippet: String): JvmCompilationResult {
   val kotlinCompilation = KotlinCompilation()
      .apply {
         sources = listOf(SourceFile.kotlin("KClass.kt", codeSnippet))
         inheritClassPath = true
         verbose = false
         messageOutputStream = ByteArrayOutputStream()
      }
   val compilationResult = kotlinCompilation.compile()
   kotlinCompilation.workingDir.deleteRecursively()

   return compilationResult
}

@OptIn(ExperimentalCompilerApi::class)
private val compiles = object : Matcher<String> {
   override fun test(value: String): MatcherResult {
      val compilationResult = compileCodeSnippet(value)
      return MatcherResult(
         compilationResult.exitCode == ExitCode.OK,
         { "Expected code to compile, but it failed to compile with error: \n${compilationResult.messages}" },
         { "Expected code to fail to compile, but it compile" }
      )
   }
}

/**
 * Assert that given codeSnippet[String] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet snippet as well.
 * @see [String.shouldNotCompile]
 * */
fun String.shouldCompile() = this should compiles

/**
 * Assert that given codeSnippet[String] does not compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet snippet as well.
 * @see [String.shouldCompile]
 * */
fun String.shouldNotCompile() = this shouldNot compiles

/**
 * Assert that given file[File] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet snippet as well.
 * @see [File.shouldNotCompile]
 * */
fun File.shouldCompile() = readText() should compiles

/**
 * Assert that given file[File] does not compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet snippet as well.
 * @see [File.shouldNotCompile]
 * */
fun File.shouldNotCompile() = readText() shouldNot compiles
