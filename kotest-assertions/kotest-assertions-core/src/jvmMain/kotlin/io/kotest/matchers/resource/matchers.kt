package io.kotest.matchers.resource

import io.kotest.matchers.ComparableMatcherResult
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
) {
   this.toLF() should matchResource(path, { s -> be(s.toLF()) }, true)
}

/**
 * Will match when given string and resource value differ
 *
 * This will ignore differences in "\r", "\n" and "\r\n", so it is not dependent on the system line separator.
 */
infix fun String.shouldNotMatchResource(
   path: String
) {
   this shouldNot matchResource(path, ::be, true)
}

fun String.shouldMatchResource(
   path: String,
   matcherProvider: (String) -> Matcher<String>,
   ignoreLineSeparators: Boolean = true
) {
   this should matchResource(path, matcherProvider, ignoreLineSeparators)
}

fun String.shouldNotMatchResource(
   path: String,
   matcherProvider: (String) -> Matcher<String>,
   ignoreLineSeparators: Boolean = true
) {
   this shouldNot matchResource(path, matcherProvider, ignoreLineSeparators)
}

fun matchResource(
   resourcePath: String,
   matcherProvider: (String) -> Matcher<String>,
   ignoreLineSeparators: Boolean
) = object : Matcher<String> {

   override fun test(value: String): MatcherResult {
      val resource = getResource(resourcePath)
      val resourceValue = resource.readText()

      val normalizedValue = if (ignoreLineSeparators) value.toLF() else value
      val normalizedResourceValue = if (ignoreLineSeparators) resourceValue.toLF() else resourceValue

      return matcherProvider(normalizedResourceValue).test(normalizedValue).let {
         ComparableMatcherResult(
            it.passed(),
            {
               val actualFilePath = normalizedValue.writeToActualValueFile(resource)

               """${it.failureMessage()}

expected to match resource, but they differed
Expected : $resourcePath
Actual   : $actualFilePath

"""
            },
            {
               """${it.negatedFailureMessage()}

expected not to match resource, but they match
Expected : $resourcePath

"""
            },
            normalizedValue,
            normalizedResourceValue,
         )
      }
   }
}

fun resourceAsString(path: String) = getResource(path).readText()

fun getResource(path: String): URL =
   object {}.javaClass.getResource(path) ?: error("Failed to get resource at $path")

private fun String?.writeToActualValueFile(resourceUrl: URL): Path =
   getActualFilePath(resourceUrl)
      .apply { writeText(this@writeToActualValueFile.toString()) }

private fun getActualFilePath(expectedFileURL: URL): Path =
   File(expectedFileURL.toURI()).let { expectedFile ->
      expectedFile.toPath()
         .parent
         .resolve("_actual")
         .createDirectories()
         .resolve(expectedFile.name)
   }

private fun String.toLF() = replace("\\r\\n?".toRegex(), "\n")
