package io.kotest.assertions.json

import io.kotest.assertions.json.comparisons.compare
import io.kotest.assertions.print.StringPrint
import io.kotest.matchers.EqualityMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.intellij.lang.annotations.Language

/**
 * Returns a [io.kotest.matchers.Matcher] that verifies that two JSON strings are equal.
 *
 * This matcher will consider two JSON strings matched if they have the same key-values pairs.
 * The [CompareJsonOptions] parameter can be used to configure the matcher behavior.
 */
fun equalJson(
   @Language("json") expected: String,
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
 * Returns a [io.kotest.matchers.Matcher] that verifies JSON trees are equal.
 *
 * This common matcher requires JSON in Kotest's [JsonNode] abstraction.
 *
 * The jvm module provides wrappers to convert from Jackson to this format.
 *
 * This matcher will consider two JSON strings matched if they have the same key-values pairs,
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

      EqualityMatcherResult(
         passed = error == null,
         actual = StringPrint.printUnquoted(value.raw),
         expected = StringPrint.printUnquoted(expected.raw),
         failureMessageFn = { "$error\n" },
         negatedFailureMessageFn = { "Expected values to not match" },
      )
   }

data class JsonTree(val root: JsonNode, val raw: String)

/**
 * Asserts that two JSON strings are equal.
 * This matcher will consider two JSON strings matched if they have the same key-values pairs, regardless of order.
 *
 * To more precisely control the comparison, use [shouldEqualJson] that accepts a [CompareJsonOptions].
 */
infix fun String.shouldEqualJson(@Language("json") expected: String): String {
   this should equalJson(expected, CompareJsonOptions())
   return this
}

/**
 * Verifies that the recevier matches the json string returned by the given block [configureAndProvideExpected].
 * The function allows configuration of [CompareJsonOptions] before returning the expected json.
 */
infix fun String.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): String {
   val options = CompareJsonOptions()
   val expected = options.configureAndProvideExpected()
   this should equalJson(expected, options)
   return this
}

infix fun String.shouldNotEqualJson(@Language("json") expected: String): String {
   this shouldNot equalJson(expected, CompareJsonOptions())
   return this
}

/**
 * Configures [CompareJsonOptions] with the given block, which should also return the expected value
 */
infix fun String.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): String {
   val options = CompareJsonOptions()
   val expected = options.configureAndProvideExpected()
   this shouldNot equalJson(expected, options)
   return this
}

infix fun String.shouldEqualSpecifiedJson(@Language("json") expected: String) {
   shouldEqualJson {
      fieldComparison = FieldComparison.Lenient
      expected
   }
}

infix fun String.shouldEqualSpecifiedJsonIgnoringOrder(@Language("json") expected: String) {
   shouldEqualJson {
      fieldComparison = FieldComparison.Lenient
      arrayOrder = ArrayOrder.Lenient
      expected
   }
}

infix fun String.shouldNotEqualSpecifiedJson(@Language("json") expected: String) {
   shouldNotEqualJson {
      fieldComparison = FieldComparison.Lenient
      expected
   }
}
