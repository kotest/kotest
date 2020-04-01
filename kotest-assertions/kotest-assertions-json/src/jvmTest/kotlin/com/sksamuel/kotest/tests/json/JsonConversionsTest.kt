package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.Json
import io.kotest.assertions.json.representation
import io.kotest.assertions.json.shouldBeJsonValueOfType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class JsonConversionsTest : FreeSpec({

    "test json representation" - {
        "test for null" {
            null.representation shouldBe """
        |null
      """.trimMargin()
        }

        "test for string" {
            """
        |null
      """.trimMargin().representation shouldBe """
        |"null"
      """.trimMargin()
        }

        "test for object" {
            """
        |{"a": "b"}
      """.trimMargin().representation shouldBe """
        |"{"a": "b"}"
      """.trimMargin()
        }
    }

    "test json value conversion" - {
        "JSON string value should be outer quoted" - {
            "correct string" - {
                "test for string" {
                    """
            |"str"
          """.trimMargin().shouldBeJsonValueOfType<String>() shouldBe "str"
                }

                "test for string with inner quotes" - {
                    """
            |"\"str\""
          """.trimMargin().shouldBeJsonValueOfType<String>() shouldBe "\"str\""
                }
            }

            "incorrect string" - {
                "test for empty string" {
                    shouldThrow<AssertionError> {
                        @Suppress("RemoveExplicitTypeArguments")
                        "".shouldBeJsonValueOfType<Any?>()
                    }
                }

                "test for string without outer quotes" {
                    shouldThrow<AssertionError> { "str".shouldBeJsonValueOfType<String>() }
                }
            }
        }

        "test for correct numbers" {
            "10".shouldBeJsonValueOfType<Int>() shouldBe 10
            "-10".shouldBeJsonValueOfType<Int>() shouldBe -10
            "10.0".shouldBeJsonValueOfType<Number>() shouldBe 10.0
            "10.0".shouldBeJsonValueOfType<Any>() shouldBe 10.0
        }

        "test for empty array" {
            "[]".shouldBeJsonValueOfType<Array<*>>() shouldBe emptyArray<Int>()
        }

        "test for nulls" {
            "null".shouldBeJsonValueOfType<Any?>() shouldBe null
            "null".shouldBeJsonValueOfType<String?>() shouldBe null
            "null".shouldBeJsonValueOfType<Int?>() shouldBe null
        }

        "test for type mismatch" {
            shouldThrow<AssertionError> { "\"str\"".shouldBeJsonValueOfType<Int?>() }
            shouldThrow<AssertionError> { "\"str\"".shouldBeJsonValueOfType<Int>() }

            shouldThrow<AssertionError> { "10".shouldBeJsonValueOfType<String>() }
        }

        "test for null" {
            shouldThrow<AssertionError> { null.shouldBeJsonValueOfType<Int?>() }
            shouldThrow<AssertionError> { null.shouldBeJsonValueOfType<Int>() }
            shouldThrow<AssertionError> { null.shouldBeJsonValueOfType<String>() }
        }

        "test for smart cast" {
            fun use(json: Json) {}

            val nullableJson: Json? = "10"
            nullableJson.shouldBeJsonValueOfType<Int>()
            use(nullableJson)
        }
  }
})
