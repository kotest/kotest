package com.sksamuel.kotest.tests.json

import io.kotest.assertions.asClue
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldMatchJsonResource
import io.kotest.assertions.json.shouldNotContainJsonKey
import io.kotest.assertions.json.shouldNotContainJsonKeyValue
import io.kotest.assertions.json.shouldNotMatchJsonResource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

const val json = """{
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
}"""

class JvmJsonAssertionsTest : StringSpec({

   "test json path" {
      json.shouldContainJsonKey("$.store.bicycle")
      json.shouldContainJsonKey("$.store.book")
      json.shouldContainJsonKey("$.store.book[0]")
      json.shouldContainJsonKey("$.store.book[0].category")
      json.shouldContainJsonKey("$.store.book[1].price")

      json.shouldNotContainJsonKey("$.store.table")

      shouldThrow<AssertionError> {
         json.shouldContainJsonKey("$.store.table")
      }.message shouldBe """{
    "store": {
        "book": [
            {... should contain the path ${'$'}.store.table"""

      shouldThrow<AssertionError> { null.shouldContainJsonKey("abc") }

      "contract should work".asClue {
         fun use(@Suppress("UNUSED_PARAMETER") json: String) {}

         val nullableJson = """{"data": "value"}"""
         nullableJson.shouldContainJsonKey("data")
         use(nullableJson)
      }
   }

   "test json key value" {
      json.shouldContainJsonKeyValue("$.store.bicycle.color", "red")
      json.shouldContainJsonKeyValue("$.store.book[0].category", "reference")
      json.shouldContainJsonKeyValue("$.store.book[0].price", 8.95)
      json.shouldContainJsonKeyValue("$.store.book[1].author", "Evelyn Waugh")
      json.shouldContainJsonKeyValue("$.store.book[1].author", "Evelyn Waugh")
      json.shouldContainJsonKeyValue("$.store.bicycle.code", 1L)

      json.shouldNotContainJsonKeyValue("$.store.book[1].author", "JK Rowling")

      shouldThrow<AssertionError> {
         json.shouldContainJsonKeyValue("$.store.book[1].author", "JK Rowling")
      }.message shouldBe """{
    "store": {
        "book": [
            {... should contain the element ${'$'}.store.book[1].author = JK Rowling"""

      shouldThrow<AssertionError> { null.shouldContainJsonKeyValue("ab", "cd") }

      "contract should work".asClue {
         fun use(@Suppress("UNUSED_PARAMETER") json: String) {}

         val nullableJson = """{"data": "value"}"""
         nullableJson.shouldContainJsonKeyValue("data", "value")
         use(nullableJson)
      }
   }

   "test json match by resource" {

      val testJson1 = """ { "name" : "sam", "location" : "chicago" } """
      val testJson2 = """ { "name" : "sam", "location" : "london" } """

      testJson1.shouldMatchJsonResource("/json1.json")
      testJson2.shouldNotMatchJsonResource("/json1.json")

      shouldThrow<AssertionError> {
         testJson2.shouldMatchJsonResource("/json1.json")
      }.message shouldBe """expected json to match, but they differed
         |
         |expected:<{"name":"sam","location":"chicago"}> but was:<{"name":"sam","location":"london"}>""".trimMargin()

      shouldThrow<AssertionError> { null shouldMatchJsonResource "/json1.json" }

      "contract should work".asClue {
         fun use(@Suppress("UNUSED_PARAMETER") json: String) {}

         val nullableJson = testJson1
         nullableJson.shouldMatchJsonResource("/json1.json")
         use(nullableJson)
      }
   }
})
