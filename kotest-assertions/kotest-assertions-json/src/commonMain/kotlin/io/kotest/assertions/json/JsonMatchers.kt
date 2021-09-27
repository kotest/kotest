package io.kotest.assertions.json

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@OptIn(ExperimentalSerializationApi::class)
internal val pretty by lazy { Json { prettyPrint = true; prettyPrintIndent = "  " } }

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
      val actualJson = try {
         value?.let(pretty::parseToJsonElement)
      } catch (ex: Exception) {
         return MatcherResult(
            false,
            { "expected: actual json to be valid json: $value" },
            {
               "expected: actual json to be invalid json: $value"
            })
      }

      val expectedJson = try {
         expected?.let(pretty::parseToJsonElement)
      } catch (ex: Exception) {
         return MatcherResult(
            false,
            { "expected: expected json to be valid json: $expected" },
            {
               "expected: expected json to be invalid json: $expected"
            })
      }

      return MatcherResult(
         actualJson == expectedJson,
         { "expected: $expectedJson but was: $actualJson" },
         {
            "expected not to match with: $expectedJson but match: $actualJson"
         })
   }
}

/**
 * Verifies that the [expected] string is valid json, and that it matches this string.
 *
 * This matcher will consider two json strings matched if they have the same key-values pairs,
 * regardless of order.
 *
 */
fun String.shouldEqualJson(expected: String, mode: CompareMode, order: CompareOrder) =
   this.shouldEqualJson(expected, legacyOptions(mode, order))

fun String.shouldEqualJson(expected: String, options: CompareJsonOptions) {
   val (e, a) = parse(expected, this)
   a should equalJson(e, options)
}

fun String.shouldNotEqualJson(expected: String, mode: CompareMode, order: CompareOrder) =
   this.shouldNotEqualJson(expected, legacyOptions(mode, order))

fun String.shouldNotEqualJson(expected: String, options: CompareJsonOptions) {
   val (e, a) = parse(expected, this)
   a shouldNot equalJson(e, options)
}

fun String.shouldBeEmptyJsonArray(): String {
   this should matchJson("[]")
   return this
}

fun String.shouldBeEmptyJsonObject(): String {
   this should matchJson("{}")
   return this
}

internal fun parse(expected: String, actual: String): Pair<JsonTree, JsonTree> {
   val enode = pretty.parseToJsonElement(expected)
   val anode = pretty.parseToJsonElement(actual)
   val e = toJsonTree(enode)
   val a = toJsonTree(anode)
   return Pair(e, a)
}

internal fun toJsonTree(root: JsonElement) =
   with(root.toJsonNode()) {
      JsonTree(this, prettyPrint(this))
   }
