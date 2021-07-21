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
fun equalJson(
   expected: JsonTree,
   mode: CompareMode,
   order: CompareOrder,
   fieldComparison: FieldComparison = FieldComparison.Exact,
) = object : Matcher<JsonTree> {
   override fun test(value: JsonTree): MatcherResult {
      val error = compare(
         path = listOf(),
         expected = expected.root,
         actual = value.root,
         mode = mode,
         order = order,
         fieldComparison
      )?.asString()

      return MatcherResult(
         error == null,
         "$error\n\nexpected:\n${expected.raw}\n\nactual:\n${value.raw}\n",
         "Expected values to not match ${expected.raw}",
      )
   }
}

data class JsonTree(val root: JsonNode, val raw: String)

infix fun String.shouldEqualJson(expected: String): Unit =
   this.shouldEqualJson(expected, CompareMode.Strict, CompareOrder.Lenient)

infix fun String.shouldNotEqualJson(expected: String): Unit =
   this.shouldNotEqualJson(expected, CompareMode.Strict, CompareOrder.Lenient)

infix fun String.shouldEqualSpecifiedJson(expected: String) =
   this.shouldEqualSpecifiedJson(expected, CompareMode.Strict)

infix fun String.shouldNotEqualSpecifiedJson(expected: String) =
   this.shouldNotEqualSpecifiedJson(expected, CompareMode.Strict)

fun String.shouldEqualJson(expected: String, mode: CompareMode) =
   shouldEqualJson(expected, mode, CompareOrder.Lenient)

fun String.shouldNotEqualJson(expected: String, mode: CompareMode) =
   shouldNotEqualJson(expected, mode, CompareOrder.Lenient)

fun String.shouldEqualJson(expected: String, order: CompareOrder) =
   shouldEqualJson(expected, CompareMode.Strict, order)

fun String.shouldNotEqualJson(expected: String, order: CompareOrder) =
   shouldNotEqualJson(expected, CompareMode.Strict, order)

fun String.shouldEqualSpecifiedJson(expected: String, mode: CompareMode) =
   shouldEqualSpecifiedJson(expected, mode, CompareOrder.Lenient)

fun String.shouldNotEqualSpecifiedJson(expected: String, mode: CompareMode) =
   shouldNotEqualSpecifiedJson(expected, mode, CompareOrder.Lenient)

fun String.shouldEqualSpecifiedJson(expected: String, order: CompareOrder) =
   shouldEqualSpecifiedJson(expected, CompareMode.Strict, order)

fun String.shouldNotEqualSpecifiedJson(expected: String, order: CompareOrder) =
   shouldNotEqualSpecifiedJson(expected, CompareMode.Strict, order)
