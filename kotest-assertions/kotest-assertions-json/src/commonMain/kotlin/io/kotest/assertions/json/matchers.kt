package io.kotest.assertions.json

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.print.printed
import io.kotest.matchers.ComparableMatcherResult
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
) = equalJson(expected, legacyOptions(mode, order))

fun equalJson(
   expected: JsonTree,
   options: CompareJsonOptions
) =
   object : Matcher<JsonTree> {
      override fun test(value: JsonTree): MatcherResult {
         val error = compare(
            path = listOf(),
            expected = expected.root,
            actual = value.root,
            options,
         )?.asString()

         return ComparableMatcherResult(
            error == null,
            { "$error\n\n" },
            { "Expected values to not match\n\n" },
            value.raw,
            expected.raw,
         )
      }
   }

data class JsonTree(val root: JsonNode, val raw: String)

infix fun String.shouldEqualJson(expected: String): Unit =
   this.shouldEqualJson(expected, defaultCompareJsonOptions)

infix fun String.shouldNotEqualJson(expected: String): Unit =
   this.shouldNotEqualJson(expected, defaultCompareJsonOptions)

fun String.shouldEqualJson(expected: String, mode: CompareMode) =
   shouldEqualJson(expected, legacyOptions(mode, CompareOrder.Strict))

fun String.shouldNotEqualJson(expected: String, mode: CompareMode) =
   shouldNotEqualJson(expected, legacyOptions(mode, CompareOrder.Strict))

fun String.shouldEqualJson(expected: String, order: CompareOrder) =
   shouldEqualJson(expected, legacyOptions(CompareMode.Strict, order))

fun String.shouldNotEqualJson(expected: String, order: CompareOrder) =
   shouldNotEqualJson(expected, legacyOptions(CompareMode.Strict, order))

infix fun String.shouldEqualSpecifiedJson(expected: String) =
   shouldEqualJson(expected, compareJsonOptions { fieldComparison = FieldComparison.Lenient })

infix fun String.shouldNotEqualSpecifiedJson(expected: String) =
   shouldNotEqualJson(expected, compareJsonOptions { fieldComparison = FieldComparison.Lenient })
