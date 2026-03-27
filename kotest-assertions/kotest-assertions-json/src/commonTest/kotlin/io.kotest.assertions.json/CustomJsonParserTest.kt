package io.kotest.assertions.json

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class CustomJsonParserTest : StringSpec({

   val lenient = Json {
      isLenient = true
      ignoreUnknownKeys = true
      prettyPrint = true
      prettyPrintIndent = "  "
   }

   val withComments = Json {
      allowComments = true
      allowTrailingComma = true
      prettyPrint = true
      prettyPrintIndent = "  "
   }

   "valid json with unquoted keys using lenient parser" {
      """{ name: "sam", age: 25 }""".shouldBeValidJson(lenient)
   }

   "unquoted keys should fail with default parser" {
      shouldThrow<AssertionError> {
         """{ name: "sam", age: 25 }""".shouldBeValidJson()
      }.message shouldBe """expected: actual json to be valid json: { name: "sam", age: 25 }"""
   }

   "json with comments using allowComments parser" {
      """
         {
            // this is a comment
            "name": "sam",
            "age": 25
         }
      """.trimIndent().shouldBeValidJson(withComments)
   }

   "comments should fail with default parser" {
      shouldThrow<AssertionError> {
         """
            {
               // this is a comment
               "name": "sam",
               "age": 25
            }
         """.trimIndent().shouldBeValidJson()
      }
   }

   "trailing comma using allowTrailingComma parser" {
      """
         {
            "name": "sam",
            "age": 25,
         }
      """.trimIndent().shouldBeValidJson(withComments)
   }

   "trailing comma should fail with default parser" {
      shouldThrow<AssertionError> {
         """
            {
               "name": "sam",
               "age": 25,
            }
         """.trimIndent().shouldBeValidJson()
      }
   }

   "shouldNotBeValidJson with default parser for strict-invalid json" {
      """{ name: "sam" }""".shouldNotBeValidJson()
   }

   "shouldNotBeValidJson should fail with lenient parser for lenient-valid json" {
      shouldThrow<AssertionError> {
         """{ name: "sam" }""".shouldNotBeValidJson(lenient)
      }.message shouldBe """expected: actual json to be invalid json: { name: "sam" }"""
   }

   "shouldBeJsonArray with trailing comma" {
      """[1, 2, 3,]""".shouldBeJsonArray(withComments)
   }

   "shouldBeJsonObject with trailing comma" {
      """{ "name": "sam", }""".shouldBeJsonObject(withComments)
   }

   "shouldEqualJson with lenient parser" {
      """{ name: "sam", age: 25 }""".shouldEqualJson("""{ "name": "sam", "age": 25 }""", lenient)
   }

   "shouldNotEqualJson with lenient parser" {
      """{ name: "sam", age: 25 }""".shouldNotEqualJson("""{ "name": "sam", "age": 30 }""", lenient)
   }

   "existing matchers without parser should still work" {
      """{"name":"sam","age":25}""".shouldBeValidJson()
      """{"name":"sam","age":25}""".shouldBeJsonObject()
      """{"name":"sam","age":25}""".shouldNotBeJsonArray()
      """[1, 2, 3]""".shouldBeJsonArray()
      """[1, 2, 3]""".shouldNotBeJsonObject()
   }

   "existing shouldEqualJson without parser should still work" {
      """{ "name": "sam", "age": 25 }""" shouldEqualJson """{ "age": 25, "name": "sam" }"""
   }
})
