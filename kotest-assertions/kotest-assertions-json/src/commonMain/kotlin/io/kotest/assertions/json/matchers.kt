package io.kotest.assertions.json

import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

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

infix fun String.shouldEqualJson(expected: String): String {
   val (e, a) = parse(expected, this)
   a should equalJson(e, CompareJsonOptions())
   return this
}

/**
 * Verifies that the recevier matches the json string returned by the given block [configureAndProvideExpected].
 * The function allows configuration of [CompareJsonOptions] before returning the expected json.
 */
infix fun String.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): String {
   val options = CompareJsonOptions()
   val expected = options.configureAndProvideExpected()
   val (e, a) = parse(expected, this)
   a should equalJson(e, options)
   return this
}

infix fun String.shouldNotEqualJson(expected: String): String {
   val (e, a) = parse(expected, this)
   a shouldNot equalJson(e, CompareJsonOptions())
   return this
}

/**
 * Configures [CompareJsonOptions] with the given block, which should also return the expected value
 */
infix fun String.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): String {
   val options = CompareJsonOptions()
   val expected = options.configureAndProvideExpected()
   val (e, a) = parse(expected, this)
   a shouldNot equalJson(e, options)
   return this
}

@Deprecated("Use shouldEqualJson which uses a lambda. Deprecated since 5.6. Will be removed in 6.0")
fun String.shouldEqualJson(expected: String, mode: CompareMode) =
   shouldEqualJson(expected, legacyOptions(mode, CompareOrder.Strict))

@Deprecated("Use shouldEqualJson which uses a lambda. Deprecated since 5.6. Will be removed in 6.0")
fun String.shouldNotEqualJson(expected: String, mode: CompareMode) =
   shouldNotEqualJson(expected, legacyOptions(mode, CompareOrder.Strict))

@Deprecated("Use shouldEqualJson which uses a lambda. Deprecated since 5.6. Will be removed in 6.0")
fun String.shouldEqualJson(expected: String, order: CompareOrder) =
   shouldEqualJson(expected, legacyOptions(CompareMode.Strict, order))

@Deprecated("Use shouldNotEqualJson which uses a lambda. Deprecated since 5.6. Will be removed in 6.0")
fun String.shouldNotEqualJson(expected: String, order: CompareOrder) =
   shouldNotEqualJson(expected, legacyOptions(CompareMode.Strict, order))

@Deprecated("Use shouldNotEqualJson which uses a lambda. Deprecated since 5.6. Will be removed in 6.0")
infix fun String.shouldEqualSpecifiedJson(expected: String) =
   shouldEqualJson(expected, compareJsonOptions { fieldComparison = FieldComparison.Lenient })

@Deprecated("Use shouldNotEqualJson which uses a lambda. Deprecated since 5.6. Will be removed in 6.0")
infix fun String.shouldNotEqualSpecifiedJson(expected: String) =
   shouldNotEqualJson(expected, compareJsonOptions { fieldComparison = FieldComparison.Lenient })
