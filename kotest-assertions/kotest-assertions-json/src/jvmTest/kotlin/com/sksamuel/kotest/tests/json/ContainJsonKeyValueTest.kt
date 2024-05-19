package com.sksamuel.kotest.tests.json

import io.kotest.assertions.asClue
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldNotContainJsonKeyValue
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class ContainJsonKeyValueTest : StringSpec({
   @Language("JSON")
   val json = """
      {
          "store": {
              "book": [
                  {
                    "category": "reference",
                    "author": "Nigel Rees",
                    "title": "Sayings of the Century",
                    "price": 8.95
                 },
                 {
                    "category": "fiction",
                    "author": "Evelyn Waugh",
                    "title": "Sword of Honour",
                    "price": 12.99
                 }
              ],
              "bicycle": {
                 "color": "red",
                 "price": 19.95,
                 "code": 1
              }
         }
      }
   """.trimIndent()

   "Negated assertions" {
      "".shouldNotContainJsonKeyValue("$.store.bicycle.color", "red")
      "{}".shouldNotContainJsonKeyValue("$.store.bicycle.color", "red")
      """{ "foo": "bar" }""".shouldNotContainJsonKeyValue("foo", "baz")
      shouldFail {
         """{ "foo": "bar" }""".shouldNotContainJsonKeyValue("foo", "bar")
      }.message shouldBe """{ "foo": "bar" } should not contain the element foo = bar"""
   }

   "Failure message states if key is missing, when it's missing" {
      shouldFail {
         json.shouldContainJsonKeyValue("$.bicycle.engine", "V2")
      }.message shouldBe """
         Expected given to contain json key <'$.bicycle.engine'> but key was not found.
      """.trimIndent()
   }

   "Failure message states states value mismatch if key is present with different value" {
      shouldFail {
         json.shouldContainJsonKeyValue("$.store.book[0].price", 9.95)
      }.message shouldBe """
         Value mismatch at '$.store.book[0].price': expected:<9.95> but was:<8.95>
      """.trimIndent()
   }

   "test json key value" {
      json.shouldContainJsonKeyValue("$.store.bicycle.color", "red")
      json.shouldContainJsonKeyValue("$.store.book[0].category", "reference")
      json.shouldContainJsonKeyValue("$.store.book[0].price", 8.95)
      json.shouldContainJsonKeyValue("$.store.book[1].author", "Evelyn Waugh")
      json.shouldContainJsonKeyValue("$.store.book[1].author", "Evelyn Waugh")
      json.shouldContainJsonKeyValue("$.store.bicycle.code", 1L)

      json.shouldNotContainJsonKeyValue("$.store.book[1].author", "JK Rowling")

      shouldThrow<AssertionError> { null.shouldContainJsonKeyValue("ab", "cd") }.message shouldBe
         "Expected a valid JSON, but was null"

      shouldThrow<AssertionError> { "".shouldContainJsonKeyValue("ab", "cd") }.message shouldBe
         "Expected a valid JSON, but was empty"

      "contract should work".asClue {
         fun use(@Suppress("UNUSED_PARAMETER") json: String) {}

         val nullableJson = """{"data": "value"}"""
         nullableJson.shouldContainJsonKeyValue("data", "value")
         use(nullableJson)
      }
   }
})
