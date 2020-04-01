package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.Json
import io.kotest.assertions.json.representation
import io.kotest.assertions.json.shouldBeJsonValueOfType
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.json.shouldMatchJsonResource
import io.kotest.assertions.json.shouldNotContainJsonKey
import io.kotest.assertions.json.shouldNotContainJsonKeyValue
import io.kotest.assertions.json.shouldNotMatchJson
import io.kotest.assertions.json.shouldNotMatchJsonResource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JsonConversionsTest : StringSpec({

  "test json representation" {
    null.representation shouldBe """null"""
    """null""".representation shouldBe """"null""""
    """{"a": "b"}""".representation shouldBe """"{"a": "b"}""""
  }

  "test json value conversion" {
    withClue("JSON string value should be quoted") {
      "\"str\"".shouldBeJsonValueOfType<String>() shouldBe "str"

      shouldThrow<AssertionError> {
        @Suppress("RemoveExplicitTypeArguments")
        "".shouldBeJsonValueOfType<Any?>()
      }

      shouldThrow<AssertionError> { "str".shouldBeJsonValueOfType<String>() }

      withClue("quotes in JSON string value should be escaped") {
        """"\"str\""""".shouldBeJsonValueOfType<String>() shouldBe "\"str\""
      }
    }

    "10".shouldBeJsonValueOfType<Int>() shouldBe 10
    "10.0".shouldBeJsonValueOfType<Number>() shouldBe 10.0
    "10.0".shouldBeJsonValueOfType<Any>() shouldBe 10.0

    "[]".shouldBeJsonValueOfType<Array<*>>() shouldBe emptyArray<Int>()

    "null".shouldBeJsonValueOfType<Any?>() shouldBe null
    "null".shouldBeJsonValueOfType<String?>() shouldBe null
    "null".shouldBeJsonValueOfType<Int?>() shouldBe null

    shouldThrow<AssertionError> { "\"str\"".shouldBeJsonValueOfType<Int?>() }
    shouldThrow<AssertionError> { "\"str\"".shouldBeJsonValueOfType<Int>() }

    shouldThrow<AssertionError> { "10".shouldBeJsonValueOfType<String>() }

    withClue("null should always throw") {
      shouldThrow<AssertionError> { null.shouldBeJsonValueOfType<Int?>() }
    }

    withClue("smart cast should work") {
      fun use(json: Json) {}

      val nullableJson: Json? = "10"
      nullableJson.shouldBeJsonValueOfType<Int>()
      use(nullableJson)
    }
  }
})
