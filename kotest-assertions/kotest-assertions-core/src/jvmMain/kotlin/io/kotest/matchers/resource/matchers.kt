package io.kotest.matchers.resource

import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.io.File
import java.net.URL
import java.nio.file.Path
import kotlin.contracts.contract
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

infix fun String?.shouldMatchResource(path: String) {
   contract {
      returns() implies (this@shouldMatchResource != null)
   }

   this should matchResource(path)
}

infix fun String?.shouldNotMatchResource(path: String) = this shouldNot matchResource(path)

fun matchResource(resourcePath: String) = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val resource = getResource(resourcePath)
      val resourceValue = resource.readText()

      return ComparableMatcherResult(
         value == resourceValue,
         {
            val actualFilePath = value.writeToActualValueFile(resource)

            """expected to match resource, but they differed
Expected : $resourcePath
Actual   : $actualFilePath

"""
         },
         {
            """expected not to match resource, but they match
Expected : $resourcePath

"""
         },
         value.toString(),
         resourceValue,
      )
   }
}

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
