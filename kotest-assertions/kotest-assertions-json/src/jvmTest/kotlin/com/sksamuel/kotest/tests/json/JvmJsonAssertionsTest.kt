package com.sksamuel.kotest.tests.json

import io.kotest.assertions.asClue
import io.kotest.assertions.json.*
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

@Language("JSON")
private const val json = """
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
"""

@EnabledIf(LinuxOnlyGithubCondition::class)
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
      }.message shouldBe
         """Expected given to contain json key <'${'$'}.store.table'> but key was not found. Found shorter valid subpath: <'${'$'}.store'>."""

      shouldThrow<AssertionError> { null.shouldContainJsonKey("abc") }

      "contract should work".asClue {
         fun use(@Suppress("UNUSED_PARAMETER") json: String) {}

         val nullableJson = """{"data": "value"}"""
         nullableJson.shouldContainJsonKey("data")
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
         |expected:<{"name":"sam","location":"chicago"}> but was:<{"name":"sam","location":"london"}>
      """.trimMargin()

      shouldThrow<AssertionError> { null shouldMatchJsonResource "/json1.json" }

      "contract should work".asClue {
         fun use(@Suppress("UNUSED_PARAMETER") json: String) {}

         val nullableJson: String? = testJson1.takeIf { it.isNotEmpty() }
         nullableJson.shouldMatchJsonResource("/json1.json")
         use(nullableJson)
      }
   }

   "matchJsonResource - property order does not matter" {
      val testJson1 = """ { "location" : "chicago", "name" : "sam" } """
      testJson1.shouldMatchJsonResource("/json1.json")
   }

   "matchJsonResource - array order matters" {
      "[1,2]" shouldMatchJsonResource "/array.json"

      shouldFail {
         "[2,1]" shouldMatchJsonResource "/array.json"
      }.message shouldBe """
         expected json to match, but they differed

         expected:<[1,2]> but was:<[2,1]>
      """.trimIndent()
   }

   "invalid json path" {
      val testJson1 = """ { "nullable" : null } """

      shouldFail { testJson1 shouldContainJsonKey "@@" }
         .message shouldBe "@@ is not a valid JSON path"

      shouldFail {
         testJson1.shouldContainJsonKeyValue(
            /* language=text */
            "@@",
            null as Any?,
         )
      }
         .message shouldBe "@@ is not a valid JSON path"
   }

   "test key with null value" {
      val testJson1 = """ { "nullable" : null } """
      testJson1.shouldContainJsonKey("$.nullable")
      testJson1.shouldContainJsonKeyValue("$.nullable", null as Any?)
   }
})
