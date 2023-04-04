package io.kotest.assertions.json

import io.kotest.common.KotestLanguage
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

/**
 * Returns a [Matcher] that verifies that two json strings are equal.
 *
 * This matcher will consider two json strings matched if they have the same key-values pairs.
 * The [CompareJsonOptions] parameter can be used to configure the matcher behavior.
 */
fun equalJson(
   @KotestLanguage("json", "", "") expected: String,
   options: CompareJsonOptions
): Matcher<String?> =
   Matcher { actual ->
      if (actual == null) {
         MatcherResult(
            expected == "null",
            { "Expected value to be equal to json '$expected', but was: null" },
            { "Expected value to be not equal to json '$expected', but was: null" }
         )
      } else {
         val (expectedTree, actualTree) = parse(expected, actual)
         equalJsonTree(expectedTree, options).test(actualTree)
      }
   }

/**
 * Returns a [Matcher] that verifies json trees are equal.
 *
 * This common matcher requires json in kotest's [JsonNode] abstraction.
 *
 * The jvm module provides wrappers to convert from Jackson to this format.
 *
 * This matcher will consider two json strings matched if they have the same key-values pairs,
 * regardless of order.
 */
private fun equalJsonTree(
   expected: JsonTree,
   options: CompareJsonOptions
): Matcher<JsonTree> =
   Matcher { value ->
      val error = compare(
         path = listOf(),
         expected = expected.root,
         actual = value.root,
         options,
      )?.asString()

      ComparableMatcherResult(
         error == null,
         { "$error\n\n" },
         { "Expected values to not match\n\n" },
         value.raw,
         expected.raw,
      )
   }

data class JsonTree(val root: JsonNode, val raw: String)

infix fun String.shouldEqualJson(expected: String): Unit =
   this.shouldEqualJson(expected, defaultCompareJsonOptions)

/**
 * Configures [CompareJsonOptions] with the given block, which should also return the expected value
 */
infix fun String.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String) {
   val options = CompareJsonOptions()
   val expected = options.configureAndProvideExpected()
   this.shouldEqualJson(expected, options)
}

infix fun String.shouldNotEqualJson(expected: String): Unit =
   this.shouldNotEqualJson(expected, defaultCompareJsonOptions)

/**
 * Configures [CompareJsonOptions] with the given block, which should also return the expected value
 */
infix fun String.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String) {
   val options = CompareJsonOptions()
   val expected = options.configureAndProvideExpected()
   this.shouldNotEqualJson(expected, options)
}

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
