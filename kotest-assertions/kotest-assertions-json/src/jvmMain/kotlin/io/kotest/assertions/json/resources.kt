package io.kotest.assertions.json

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.intellijFormatError
import io.kotest.assertions.print.printed
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.contract

infix fun String?.shouldMatchJsonResource(resource: String) {
   contract {
      returns() implies (this@shouldMatchJsonResource != null)
   }

   this should matchJsonResource(resource)
}

infix fun String.shouldNotMatchJsonResource(resource: String) = this shouldNot matchJsonResource(resource)

fun matchJsonResource(resource: String) = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val actualJson = value?.let(pretty::parseToJsonElement)
      val expectedJson = this.javaClass.getResourceAsStream(resource).bufferedReader().use {
         pretty.parseToJsonElement(it.readText())
      }

      return MatcherResult(
         actualJson == expectedJson,
         {
            intellijFormatError(
               Expected(expectedJson.toString().printed()),
               Actual(actualJson.toString().printed())
            )
         },
         {
            "expected not to match with: $expectedJson but match: $actualJson"
         })
   }
}
