package io.kotest.assertions.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

val mapper by lazy { ObjectMapper().registerKotlinModule() }

/**
 * Verifies that the [expected] string is valid json, and that it matches this string.
 *
 * This matcher will consider two json strings matched if they have the same key-values pairs,
 * regardless of order.
 *
 */
infix fun String?.shouldMatchJson(expected: String?) = this should matchJson(expected)
infix fun String?.shouldNotMatchJson(expected: String?) = this shouldNot matchJson(expected)
fun matchJson(expected: String?) = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val actualJson = value?.let(mapper::readTree)
      val expectedJson = expected?.let(mapper::readTree)

      return MatcherResult(
         actualJson == expectedJson,
         "expected: $expectedJson but was: $actualJson",
         "expected not to match with: $expectedJson but match: $actualJson"
      )
   }
}

/**
 * Verifies that the [expected] string is valid json, and that it matches this string.
 *
 * This matcher will consider two json strings matched if they have the same key-values pairs,
 * regardless of order.
 *
 */
actual fun String.shouldEqualJson(expected: String, mode: CompareMode) {
   val anode = mapper.readTree(this)
   val bnode = mapper.readTree(expected)
   val a = JsonTree(anode.toJsonNode(), anode.toPrettyString())
   val e = JsonTree(bnode.toJsonNode(), bnode.toPrettyString())
   a should equalJson(e, mode)
}

actual fun String.shouldNotEqualJson(expected: String, mode: CompareMode) {
   val anode = mapper.readTree(this)
   val bnode = mapper.readTree(expected)
   val a = JsonTree(anode.toJsonNode(), anode.toPrettyString())
   val e = JsonTree(bnode.toJsonNode(), bnode.toPrettyString())
   a shouldNot equalJson(e, mode)
}
