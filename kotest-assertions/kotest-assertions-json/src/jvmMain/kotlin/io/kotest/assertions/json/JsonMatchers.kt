package io.kotest.assertions.json

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.serialization.json.Json

val pretty by lazy { Json { prettyPrint = true } }

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
      val actualJson = value?.let(pretty::parseToJsonElement)
      val expectedJson = expected?.let(pretty::parseToJsonElement)

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
actual fun String.shouldEqualJson(expected: String, mode: CompareMode, order: CompareOrder) {
   val (e, a) = parse(expected, this)
   a should equalJson(e, mode, order)
}

actual fun String.shouldNotEqualJson(expected: String, mode: CompareMode, order: CompareOrder) {
   val (e, a) = parse(expected, this)
   a shouldNot equalJson(e, mode, order)
}

internal fun parse(expected: String, actual: String): Pair<JsonTree, JsonTree> {
   val enode = pretty.parseToJsonElement(expected)
   val anode = pretty.parseToJsonElement(actual)
   val e = JsonTree(enode.toJsonNode(), enode.toString())
   val a = JsonTree(anode.toJsonNode(), anode.toString())
   return Pair(e, a)
}
