package io.kotest.matchers.resource

import io.kotest.assertions.print.StringPrint
import io.kotest.matchers.ComparisonMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.be
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.io.File
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

/**
 * Will match when given String and resource value are equal
 *
 * This will ignore differences in "\r", "\n" and "\r\n", so it is not dependent on the system line separator.
 */
infix fun String.shouldMatchResource(
   path: String
): String {
   this should matchResource(path, ::be, ignoreLineSeparators = true, trim = false)
   return this
}

/**
 * Will match when given string and resource value differ
 *
 * This will ignore differences in "\r", "\n" and "\r\n", so it is not dependent on the system line separator.
 */
infix fun String.shouldNotMatchResource(
   path: String
): String {
   this shouldNot matchResource(path, ::be, ignoreLineSeparators = true, trim = false)
   return this
}

/**
 * Will match if the given String and the resource value matches using matcher provided by [matcherProvider]
 *
 * @param ignoreLineSeparators if true, will ignore differences in "\r", "\n" and "\r\n", so it is not dependent on the system line separator.
 * @param trim if true, will trim the expected and actual values before comparing them.
 */
fun String.shouldMatchResource(
   path: String,
   matcherProvider: (String) -> Matcher<String> = ::be,
   ignoreLineSeparators: Boolean = true,
   trim: Boolean = false,
): String {
   this should matchResource(
      resourcePath = path,
      matcherProvider = matcherProvider,
      ignoreLineSeparators = ignoreLineSeparators,
      trim = trim,
   )
   return this
}

/**
 * Will match if the given String and the resource value **not** matches using matcher provided by [matcherProvider]
 */
fun String.shouldNotMatchResource(
   path: String,
   matcherProvider: (String) -> Matcher<String> = ::be,
   ignoreLineSeparators: Boolean = true,
   trim: Boolean = false,
): String {
   this shouldNot matchResource(
      resourcePath = path,
      matcherProvider = matcherProvider,
      ignoreLineSeparators = ignoreLineSeparators,
      trim = trim
   )
   return this
}

fun matchResource(
   resourcePath: String,
   matcherProvider: (String) -> Matcher<String>,
   ignoreLineSeparators: Boolean,
   trim: Boolean,
) = object : Matcher<String> {

   override fun test(value: String): MatcherResult {
      val expectedUrl = getResource(resourcePath)
      val expected = expectedUrl.readText()

      val normalizedActual = if (ignoreLineSeparators) value.toLF() else value
      val normalizedExpected = if (ignoreLineSeparators) expected.toLF() else expected

      val trimmedActual = if (trim) normalizedActual.trim() else normalizedActual
      val trimmedExpected = if (trim) normalizedExpected.trim() else normalizedExpected

      return matcherProvider(trimmedExpected).test(trimmedActual).let {
         ComparisonMatcherResult(
            passed = it.passed(),
            actual = StringPrint.printUnquoted(trimmedActual),
            expected = StringPrint.printUnquoted(trimmedExpected),
            failureMessageFn = {

               val actualFilePath = normalizedActual.writeToActualValueFile(expectedUrl)

               """${it.failureMessage()}

            expected to match resource, but they differed
            Expected : $resourcePath
            Actual   : $actualFilePath"""
            },
            negatedFailureMessageFn = {
               """${it.negatedFailureMessage()}

            expected not to match resource, but they match
            Expected : $resourcePath"""
            },
         )
      }
   }
}

internal fun resourceAsString(path: String) = getResource(path).readText()

internal fun getResource(path: String): URL =
   object {}.javaClass.getResource(path) ?: error("Failed to get resource at $path")

private fun String?.writeToActualValueFile(resourceUrl: URL): Path =
   getActualFilePath(resourceUrl)
      .apply { writeText(this@writeToActualValueFile.toString()) }

internal fun getActualFilePath(expectedFileURL: URL): Path =
   File(expectedFileURL.toURI()).let { expectedFile ->
      expectedFile.toPath()
         .parent
         .resolve("_actual")
         .createDirectories()
         .resolve(expectedFile.name)
   }

private fun String.toLF() = replace("\\r\\n?".toRegex(), "\n")
