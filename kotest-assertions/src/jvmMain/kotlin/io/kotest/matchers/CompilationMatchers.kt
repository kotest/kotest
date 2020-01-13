package io.kotest.matchers

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import io.kotest.*
import java.io.File

private fun compileCodeSnippet(codeSnippet: String): Result {
   val testSourceFile = SourceFile.kotlin("KClass.kt", codeSnippet)

   return KotlinCompilation()
      .apply {
         sources = listOf(testSourceFile)
         inheritClassPath = true
      }
      .compile()
}

private val compiles = object : Matcher<String> {
   override fun test(value: String): MatcherResult {
      val compilationResult = compileCodeSnippet(value)
      return MatcherResult(
         compilationResult.exitCode == ExitCode.OK,
         { "Expected code to compile, but it failed to compile" },
         { "Expected code to fail to compile, but it compile" }
      )
   }
}

fun String.shouldCompiles() = this should compiles

fun String.shouldNotCompiles() = this shouldNot compiles

fun File.shouldCompiles() = readText() should compiles

fun File.shouldNotCompiles() = readText() shouldNot compiles
