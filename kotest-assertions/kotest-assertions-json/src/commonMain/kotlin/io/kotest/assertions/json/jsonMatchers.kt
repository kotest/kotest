package io.kotest.assertions.json

import io.kotest.assertions.print.StringPrint
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResultBuilder
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class)
internal val pretty by lazy { Json { prettyPrint = true; prettyPrintIndent = "  " } }

fun matchJson(@Language("json") expected: String?) = object : Matcher<String?> {
   override fun test(value: String?): MatcherResult {
      val actualJson = try {
         value?.let(pretty::parseToJsonElement)
      } catch (_: Exception) {
         return MatcherResultBuilder.create(false)
            .withFailureMessage   { "expected: actual json to be valid json: $value" }
            .withNegatedFailureMessage  { "expected: actual json to be invalid json: $value" }
            .build()
      }

      val expectedJson = try {
         expected?.let(pretty::parseToJsonElement)
      } catch (_: Exception) {
         return MatcherResultBuilder.create(false)
            .withFailureMessage { "expected: expected json to be valid json: $expected" }
            .withNegatedFailureMessage { "expected: expected json to be invalid json: $expected" }
            .build()
      }

      return MatcherResultBuilder.create(actualJson == expectedJson)
         .withValues(
            actual = { StringPrint.printUnquoted(actualJson.toString()) },
            expected = { StringPrint.printUnquoted(expectedJson.toString()) }
         ).withFailureMessage { "expected json to match, but they differed\n" }
         .withNegatedFailureMessage { "expected not to match with: $expectedJson but match: $actualJson" }
         .build()
   }
}

fun beValidJson() = object : Matcher<String?> {
   override fun test(value: String?): MatcherResult {
      return try {
         value?.let(pretty::parseToJsonElement)
         MatcherResultBuilder.create(true)
            .withFailureMessage { "expected: actual json to be valid json: $value" }
            .withNegatedFailureMessage { "expected: actual json to be invalid json: $value" }
            .build()
      } catch (_: Exception) {
         MatcherResultBuilder.create(false)
            .withFailureMessage { "expected: actual json to be valid json: $value" }
            .withNegatedFailureMessage { "expected: actual json to be invalid json: $value" }
            .build()
      }
   }
}

fun beJsonType(kClass: KClass<*>) = object : Matcher<String?> {

   override fun test(value: String?): MatcherResult {
      val element = try {
         value?.let(pretty::parseToJsonElement)
      } catch (_: Exception) {
         return MatcherResultBuilder.create(false)
            .withFailureMessage { "expected: actual json to be valid json: $value" }
            .withNegatedFailureMessage { "expected: actual json to be invalid json: $value" }
            .build()
      }
      return MatcherResultBuilder.create(kClass.isInstance(element))
         .withFailureMessage { "expected: $value to be valid json of type: ${kClass.simpleName}" }
         .withNegatedFailureMessage { "expected: $value to not be of type: ${kClass.simpleName}" }
         .build()
   }
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
   this should beJsonArray()
   return this
}

fun String.shouldNotBeJsonArray(): String {
   this shouldNot beJsonArray()
   return this
}

fun beJsonArray() = beJsonType(JsonArray::class)

fun String.shouldBeJsonObject(): String {
   this should beJsonObject()
   return this
}

fun String.shouldNotBeJsonObject(): String {
   this shouldNot beJsonObject()
   return this
}

fun beJsonObject() = beJsonType(JsonObject::class)

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
