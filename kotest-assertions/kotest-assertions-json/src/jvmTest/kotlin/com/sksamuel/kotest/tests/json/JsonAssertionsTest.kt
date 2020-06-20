package com.sksamuel.kotest.tests.json

import io.kotest.assertions.asClue
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.json.shouldMatchJsonResource
import io.kotest.assertions.json.shouldNotContainJsonKey
import io.kotest.assertions.json.shouldNotContainJsonKeyValue
import io.kotest.assertions.json.shouldNotMatchJson
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
            "price": 19.95
        }
    }
}"""

class JsonAssertionsTest : StringSpec({

  val json1 = """ { "name" : "sam", "location" : "london" } """
  val json2 = """ { "location": "london", "name" : "sam" } """
  val json3 = """ { "location": "chicago", "name" : "sam" } """

  "test json equality" {
    json1.shouldMatchJson(json2)
    json1.shouldNotMatchJson(json3)

    null.shouldMatchJson(null)
    null.shouldNotMatchJson(json1)
    json1.shouldNotMatchJson(null)

    shouldThrow<AssertionError> { null.shouldNotMatchJson(null) }
    shouldThrow<AssertionError> { null.shouldMatchJson(json1) }
    shouldThrow<AssertionError> { json1.shouldMatchJson(null) }
  }

  "should return correct error message on failure" {
    shouldThrow<AssertionError> {
      json1 shouldMatchJson json3
    }.message shouldBe """expected: {"location":"chicago","name":"sam"} but was: {"name":"sam","location":"london"}"""

    shouldThrow<AssertionError> {
      json1 shouldNotMatchJson json2
    }.message shouldBe """expected not to match with: {"location":"london","name":"sam"} but match: {"name":"sam","location":"london"}"""
  }

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

      val nullableJson: String? = """{"data": "value"}"""
      nullableJson.shouldContainJsonKey("data")
      use(nullableJson)
    }
  }

  "test json key value" {
    json.shouldContainJsonKeyValue("$.store.bicycle.color", "red")
    json.shouldContainJsonKeyValue("$.store.book[0].category", "reference")
    json.shouldContainJsonKeyValue("$.store.book[0].price", 8.95)
    json.shouldContainJsonKeyValue("$.store.book[1].author", "Evelyn Waugh")

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

      val nullableJson: String? = """{"data": "value"}"""
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
    }.message shouldBe """expected: {"name":"sam","location":"chicago"} but was: {"name":"sam","location":"london"}"""

    shouldThrow<AssertionError> { null shouldMatchJsonResource "/json1.json" }

    "contract should work".asClue {
      fun use(@Suppress("UNUSED_PARAMETER") json: String) {}

      val nullableJson: String? = testJson1
      nullableJson.shouldMatchJsonResource("/json1.json")
      use(nullableJson)
    }
  }
})
