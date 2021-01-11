package io.kotest.assertions.json

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
infix fun String?.shouldMatchJsonResource(resource: String) {
   contract {
      returns() implies (this@shouldMatchJsonResource != null)
   }

   this should matchJsonResource(resource)
}

infix fun String.shouldNotMatchJsonResource(resource: String) = this shouldNot matchJsonResource(resource)
fun matchJsonResource(resource: String) = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val actualJson = value?.let(mapper::readTree)
      val expectedJson = mapper.readTree(this.javaClass.getResourceAsStream(resource))

      return MatcherResult(
         actualJson == expectedJson,
         "expected: $expectedJson but was: $actualJson",
         "expected not to match with: $expectedJson but match: $actualJson"
      )
   }
}
