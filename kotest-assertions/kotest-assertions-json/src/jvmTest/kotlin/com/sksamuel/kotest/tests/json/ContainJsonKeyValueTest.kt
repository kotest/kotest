package com.sksamuel.kotest.tests.json

import io.kotest.assertions.asClue
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldNotContainJsonKeyValue
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

@EnabledIf(LinuxCondition::class)
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
                    "price": 12.99,
                    "comments": []
                 }
              ],
              "bicycle": {
                 "color": "red",
                 "price": 19.95,
                 "code": 1,
                 "weight": null
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
      }.message shouldBe "Expected given to contain json key <'$.bicycle.engine'> but key was not found. "
   }

   "Failure message states if key is missing, shows valid subpath" {
      shouldFail {
         json.shouldContainJsonKeyValue("$.store.bicycle.engine", "V2")
      }.message shouldBe """
         Expected given to contain json key <'$.store.bicycle.engine'> but key was not found. Found shorter valid subpath: <'$.store.bicycle'>.
      """.trimIndent()
   }

   "Failure message states if key is missing, shows json array index out of bounds" {
      shouldFail {
         json.shouldContainJsonKeyValue("$.store.book[2].category", "V2")
      }.message shouldBe "Expected given to contain json key <'$.store.book[2].category'> but key was not found. The array at path <'$.store.book'> has size 2, so index 2 is out of bounds."
   }

   "Failure message states if key is missing, shows element is not json array" {
      shouldFail {
         json.shouldContainJsonKeyValue("$.store.bicycle[0].color", "V2")
      }.message shouldBe "Expected given to contain json key <'$.store.bicycle[0].color'> but key was not found. Found shorter valid subpath: <'$.store'>."
   }

   "Failure message states if key is missing, shows json array index out of bounds when array is empty" {
      shouldFail {
         json.shouldContainJsonKeyValue("$.store.book[1].comments[0]", "V2")
      }.message shouldBe "Expected given to contain json key <'$.store.book[1].comments[0]'> but key was not found. The array at path <'$.store.book[1].comments'> has size 0, so index 0 is out of bounds."
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
      json.shouldContainJsonKeyValue<Int?>("$.store.bicycle.weight", null)

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

   "Should pass json key-value assertion for values with implicit type" {
      """{ "value": 42 }""".shouldContainJsonKeyValue<Any>("$.value", 42L)
      """{ "value": 0.46479126999899145 }""".shouldContainJsonKeyValue<Any>("$.value", 0.46479126999899145)
      """{ "value": 3.14 }""".shouldContainJsonKeyValue<Any>("$.value", 3.14f)
      """{ "value": null }""".shouldContainJsonKeyValue<Any?>("$.value", null)
   }
})
