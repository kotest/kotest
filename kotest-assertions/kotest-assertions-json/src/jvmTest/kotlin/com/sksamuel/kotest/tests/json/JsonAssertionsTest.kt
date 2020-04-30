package com.sksamuel.kotest.tests.json

import io.kotest.assertions.asClue
import io.kotest.assertions.json.Json
import io.kotest.assertions.json.jsonKeyValueEntries
import io.kotest.assertions.json.shouldContainExactly
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyAndValueOfSpecificType
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldContainOnlyJsonKey
import io.kotest.assertions.json.shouldContainOnlyJsonKeyAndValueOfSpecificType
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
    json.shouldContainJsonKey("$.store.bicycle") shouldMatchJson """{"color": "red", "price": 19.95}"""
    json.shouldContainJsonKey("$.store.book") shouldMatchJson """
      [
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
      ]
    """
    json.shouldContainJsonKey("$.store.book[0]") shouldMatchJson """
      {
          "category": "reference",
          "author": "Nigel Rees",
          "title": "Sayings of the Century",
          "price": 8.95
      }
    """
    json.shouldContainJsonKey("$.store.book[0].category") shouldBe "\"reference\""
    json.shouldContainJsonKey("$.store.book[1].price") shouldBe "12.99"

    json.shouldNotContainJsonKey("$.store.table")

    shouldThrow<AssertionError> { null.shouldContainJsonKey("abc") }

    shouldThrow<AssertionError> {
      json.shouldContainJsonKey("$.store.table")
    }.message shouldBe """"{
    "store": {
        "book": [
            {..." should contain the path ${'$'}.store.table"""

    """{"data": null}""" shouldContainJsonKey "data" shouldBe "null"

    "contract should work".asClue {
      fun use(@Suppress("UNUSED_PARAMETER") json: Json) {}

      val nullableJson: Json? = """{"data": null}"""
      nullableJson.shouldContainJsonKey("data")  // todo: use infix form after https://youtrack.jetbrains.com/issue/KT-27261 is resolved
      use(nullableJson)
    }
  }

  "test json key value" {
    json.shouldContainJsonKeyValue("$.store.bicycle.color", "red")
    json.shouldContainJsonKeyValue("$.store.book[0].category", "reference")
    json.shouldContainJsonKeyValue("$.store.book[0].price", 8.95)
    json.shouldContainJsonKeyValue("$.store.book[1].author", "Evelyn Waugh")

    shouldThrow<AssertionError> { null.shouldContainJsonKeyValue("ab", "cd") }

    json.shouldNotContainJsonKeyValue("$.store.book[1].author", "JK Rowling")

    shouldThrow<AssertionError> {
      json.shouldContainJsonKeyValue("$.store.book[1].author", "JK Rowling")
    }.message shouldBe """"{
    "store": {
        "book": [
            {..." should contain the element ${'$'}.store.book[1].author = JK Rowling"""

    "contract should work".asClue {
      fun use(@Suppress("UNUSED_PARAMETER") json: Json) {}

      val nullableJson: Json? = """{"data": "value"}"""
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
  }

  "test json contains countable elements" {
    """{}""" shouldContainExactly 0.jsonKeyValueEntries
    """{"a": 1}""" shouldContainExactly 1.jsonKeyValueEntries
    """{"a": 1, "b": 2}""" shouldContainExactly 2.jsonKeyValueEntries

    shouldThrow<AssertionError> { """{}""" shouldContainExactly 1.jsonKeyValueEntries }
    shouldThrow<AssertionError> { """{"a": 1}""" shouldContainExactly 2.jsonKeyValueEntries }
    shouldThrow<AssertionError> { """{"a": 1, "b": 2}""" shouldContainExactly 1.jsonKeyValueEntries }

    shouldThrow<AssertionError> { null shouldContainExactly 0.jsonKeyValueEntries }
    shouldThrow<AssertionError> { null shouldContainExactly 20.jsonKeyValueEntries }

    shouldThrow<AssertionError> { """"string"""" shouldContainExactly 6.jsonKeyValueEntries }
    shouldThrow<AssertionError> { """["array elem"]""" shouldContainExactly 1.jsonKeyValueEntries }

    "contract should work".asClue {
      fun use(@Suppress("UNUSED_PARAMETER") json: Json) {}

      val nullableJson: Json? = """{}"""
      nullableJson.shouldContainExactly(0.jsonKeyValueEntries)  // todo: use infix form after https://youtrack.jetbrains.com/issue/KT-27261 is resolved
      use(nullableJson)
    }
  }

  "test json contains key and value of specific type" {
    """{"c": null}""".shouldContainJsonKeyAndValueOfSpecificType<Int?>("c") shouldBe null
    """{"a": 1}""".shouldContainJsonKeyAndValueOfSpecificType<Int>("a") shouldBe 1
    """{"a": 1, "b": "2"}""".shouldContainJsonKeyAndValueOfSpecificType<String?>("b") shouldBe "2"

    shouldThrow<AssertionError> { """{}""".shouldContainJsonKeyAndValueOfSpecificType<Int>("a") }
    shouldThrow<AssertionError> { """{"a": 1}""".shouldContainJsonKeyAndValueOfSpecificType<String>("a") }

    shouldThrow<AssertionError> { null.shouldContainJsonKeyAndValueOfSpecificType<Int>("a") }
    shouldThrow<AssertionError> { null.shouldContainJsonKeyAndValueOfSpecificType<Int?>("a") }

    shouldThrow<AssertionError> { """"string"""".shouldContainJsonKeyAndValueOfSpecificType<Int>("a") }
    shouldThrow<AssertionError> { """["array elem"]""".shouldContainJsonKeyAndValueOfSpecificType<Int>("a") }

    "contract should work".asClue {
      fun use(@Suppress("UNUSED_PARAMETER") json: Json) {}

      val nullableJson: Json? = """{"a": 1}"""
      nullableJson.shouldContainJsonKeyAndValueOfSpecificType<Int>("a")
      use(nullableJson)
    }
  }

  "test json contains single key" {
    """{"c": null}""".shouldContainOnlyJsonKey("c") shouldBe "null"
    """{"c": []}""".shouldContainOnlyJsonKey("c") shouldBe "[]"
    """{"c": "abc"}""".shouldContainOnlyJsonKey("c") shouldBe "\"abc\""

    shouldThrow<AssertionError> { "" shouldContainOnlyJsonKey "c" }
    shouldThrow<AssertionError> { """{"a": 1, "b": "2"}""" shouldContainOnlyJsonKey "a" }


    shouldThrow<AssertionError> { null shouldContainOnlyJsonKey "abc" }

    "contract should work".asClue {
      fun use(@Suppress("UNUSED_PARAMETER") json: Json) {}

      val nullableJson: Json? = """{"a": 1}"""
      nullableJson.shouldContainOnlyJsonKey("a")  // todo: use infix form after https://youtrack.jetbrains.com/issue/KT-27261 is resolved
      use(nullableJson)
    }
  }

  "test json contains single key and value of specific type" {
    """{"c": null}""".shouldContainOnlyJsonKeyAndValueOfSpecificType<Int?>("c") shouldBe null
    """{"c": 22}""".shouldContainOnlyJsonKeyAndValueOfSpecificType<Int?>("c") shouldBe 22
    """{"c": 22}""".shouldContainOnlyJsonKeyAndValueOfSpecificType<Int>("c") shouldBe 22
//    """{"c": []}""".shouldContainOnlyJsonKeyAndValueOfSpecificType<Array<Int>>("c") shouldBe emptyArray()  // todo: fix this case
    """{"c": "abc"}""".shouldContainOnlyJsonKeyAndValueOfSpecificType<String>("c") shouldBe "abc"

    shouldThrow<AssertionError> { "".shouldContainOnlyJsonKeyAndValueOfSpecificType<Int?>("c") }
    shouldThrow<AssertionError> { """{"c": null}""".shouldContainOnlyJsonKeyAndValueOfSpecificType<Int>("c") }
    shouldThrow<AssertionError> { """{"a": 1, "b": "2"}""".shouldContainOnlyJsonKeyAndValueOfSpecificType<Int?>("a") }

    shouldThrow<AssertionError> { null.shouldContainOnlyJsonKeyAndValueOfSpecificType<Int?>("c") }

    "contract should work".asClue {
      fun use(@Suppress("UNUSED_PARAMETER") json: Json) {}

      val nullableJson: Json? = """{"a": 1}"""
      nullableJson.shouldContainOnlyJsonKeyAndValueOfSpecificType<Int?>("a")  // todo: use infix form after https://youtrack.jetbrains.com/issue/KT-27261 is resolved
      use(nullableJson)
    }
  }
})
