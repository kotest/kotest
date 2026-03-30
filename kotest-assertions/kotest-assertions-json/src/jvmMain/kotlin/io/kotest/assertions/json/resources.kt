package io.kotest.assertions.json

import io.kotest.assertions.print.StringPrint
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResultBuilder
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import kotlin.contracts.contract

infix fun String?.shouldMatchJsonResource(@Language("file-reference") resource: String) {
   contract {
      returns() implies (this@shouldMatchJsonResource != null)
   }

   this should matchJsonResource(resource)
}

fun String?.shouldMatchJsonResource(@Language("file-reference") resource: String, parser: Json) {
   contract {
      returns() implies (this@shouldMatchJsonResource != null)
   }

   this should matchJsonResource(resource, parser)
}

infix fun String.shouldNotMatchJsonResource(@Language("file-reference") resource: String) =
   this shouldNot matchJsonResource(resource)

fun String.shouldNotMatchJsonResource(@Language("file-reference") resource: String, parser: Json) =
   this shouldNot matchJsonResource(resource, parser)

fun matchJsonResource(resource: String): Matcher<String?> = matchJsonResource(resource, pretty)

fun matchJsonResource(resource: String, parser: Json): Matcher<String?> = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val actualJson = value?.let(parser::parseToJsonElement)
      val expectedJson = this.javaClass.getResourceAsStream(resource)?.bufferedReader()?.use {
         parser.parseToJsonElement(it.readText())
      } ?: throw AssertionError("File should exist in resources: $resource")

      return MatcherResultBuilder.create(actualJson == expectedJson)
         .withValues(
            expected = { StringPrint.printUnquoted(expectedJson.toString()) },
            actual = { StringPrint.printUnquoted(actualJson.toString()) })
         .withFailureMessage { "expected json to match, but they differed\n" }
         .withNegatedFailureMessage { "expected not to match with: $expectedJson but match: $actualJson" }
         .build()
   }
}
