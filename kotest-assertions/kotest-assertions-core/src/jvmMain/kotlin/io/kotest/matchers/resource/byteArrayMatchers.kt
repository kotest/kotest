package io.kotest.matchers.resource

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.be
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.writeBytes


/**
 * Will match when given ByteArray and resource value are equal
 */
infix fun ByteArray.shouldMatchResource(
   path: String
) {
   this should matchResource(path, ::be)
}

/**
 * Will match when given ByteArray and resource value differ
 */
infix fun ByteArray.shouldNotMatchResource(
   path: String
) {
   this shouldNot matchResource(path, ::be)
}

fun ByteArray.shouldMatchResource(
   path: String,
   matcherProvider: (ByteArray) -> Matcher<ByteArray>
) {
   this should matchResource(path, matcherProvider)
}

fun ByteArray.shouldNotMatchResource(
   path: String,
   matcherProvider: (ByteArray) -> Matcher<ByteArray>
) {
   this shouldNot matchResource(path, matcherProvider)
}

fun matchResource(
   resourcePath: String,
   matcherProvider: (ByteArray) -> Matcher<ByteArray>,
) = object : Matcher<ByteArray> {

   override fun test(value: ByteArray): MatcherResult {
      val resource = getResource(resourcePath)
      val resourceValue = resource.readBytes()

      return matcherProvider(resourceValue).test(value).let {
         MatcherResult(
            it.passed(),
            {
               val actualFilePath = value.writeToActualValueFile(resource)

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
            }
         )
      }
   }
}

fun resourceAsBytes(path: String) = getResource(path).readBytes()

private fun ByteArray?.writeToActualValueFile(resourceUrl: URL): Path =
   getActualFilePath(resourceUrl)
      .apply { writeBytes(this@writeToActualValueFile ?: ByteArray(0)) }
