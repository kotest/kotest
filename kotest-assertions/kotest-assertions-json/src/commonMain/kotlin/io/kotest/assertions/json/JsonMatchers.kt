package io.kotest.assertions.json

import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

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

@OptIn(ExperimentalSerializationApi::class)
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
            { "expected: expected json to be invalid json: $expected" }
         )
      }

      return ComparableMatcherResult(
         actualJson == expectedJson,
         { "expected json to match, but they differed\n\n" },
         { "expected not to match with: $expectedJson but match: $actualJson" },
         actualJson.toString(),
         expectedJson.toString()
      )
   }
}

@OptIn(ExperimentalSerializationApi::class)
fun beValidJson() = object : Matcher<String?> {
   override fun test(value: String?): MatcherResult {
      return try {
         value?.let(pretty::parseToJsonElement)
         MatcherResult(
              true,
              { "expected: actual json to be valid json: $value" },
              { "expected: actual json to be invalid json: $value" }
         )
      } catch (ex: Exception) {
         MatcherResult(
              false,
              { "expected: actual json to be valid json: $value" },
              { "expected: actual json to be invalid json: $value" }
         )
      }
   }
}

@OptIn(ExperimentalSerializationApi::class)
fun beElementOf(kClass: KClass<*>) = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val element = try {
         value?.let(pretty::parseToJsonElement)
      } catch (ex: Exception) {
         return MatcherResult(
            false,
            { "expected: actual json to be valid json: $value" },
            { "expected: actual json to be invalid json: $value" }
         )
      }
      return MatcherResult(
         kClass.isInstance(element),
         { "expected: $value to be valid json of type: ${kClass.simpleName}" },
         { "expected: $value to not be of type: ${kClass.simpleName}" }
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

fun String.shouldBeJsonArray(): String {
   this should beElementOf(JsonArray::class)
   return this
}

fun String.shouldNotBeJsonArray(): String {
   this shouldNot beElementOf(JsonArray::class)
   return this
}

fun String.shouldBeJsonObject(): String {
   this should beElementOf(JsonObject::class)
   return this
}

fun String.shouldNotBeJsonObject(): String {
   this shouldNot beElementOf(JsonObject::class)
   return this
}

fun String.shouldBeValidJson(): String {
   this should beValidJson()
   return this
}

fun String.shouldNotBeValidJson(): String {
   this shouldNot beValidJson()
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
