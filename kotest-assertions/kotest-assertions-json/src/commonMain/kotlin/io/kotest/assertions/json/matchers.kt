package io.kotest.assertions.json

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

/**
 * Returns a [Matcher] that verifies json trees are equal.
 *
 * This common matcher requires json in kotest's [JsonNode] abstraction.
 *
 * The jvm module provides wrappers to convert from Jackson to this format.
 *
 * This matcher will consider two json strings matched if they have the same key-values pairs,
 * regardless of order.
 *
 */
fun equalJson(expected: Json, mode: CompareMode) = object : Matcher<Json> {
   override fun test(value: Json): MatcherResult {
      val error = compare(expected.tree, value.tree, mode)?.asString()
      return MatcherResult(
         error == null,
         "$error\n\nexpected:\n${expected.raw}\n\nactual:\n${value.raw}\n",
         "Expected values to not match ${expected.raw}",
      )
   }
}

data class Json(val tree: JsonNode, val raw: String)
