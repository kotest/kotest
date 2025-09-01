package io.kotest.assertions.json

import io.kotest.assertions.print.StringPrint
import io.kotest.matchers.ComparisonMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.intellij.lang.annotations.Language
import kotlin.contracts.contract

infix fun String?.shouldMatchJsonResource(@Language("file-reference") resource: String) {
   contract {
      returns() implies (this@shouldMatchJsonResource != null)
   }

   this should matchJsonResource(resource)
}

infix fun String.shouldNotMatchJsonResource(@Language("file-reference") resource: String) =
   this shouldNot matchJsonResource(resource)

fun matchJsonResource(resource: String) = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val actualJson = value?.let(pretty::parseToJsonElement)
      val expectedJson = this.javaClass.getResourceAsStream(resource)?.bufferedReader()?.use {
         pretty.parseToJsonElement(it.readText())
      } ?: throw AssertionError("File should exist in resources: $resource")

      return ComparisonMatcherResult(
         passed = actualJson == expectedJson,
         actual = StringPrint.printUnquoted(actualJson.toString()),
         expected = StringPrint.printUnquoted(expectedJson.toString()),
         failureMessageFn = { "expected json to match, but they differed\n" },
         negatedFailureMessageFn = { "expected not to match with: $expectedJson but match: $actualJson" },
      )
   }
}
