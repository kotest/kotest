package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalKotest::class)
class JsonSchemaNumberTest : FunSpec(
   {
      context("multipleOf") {
         val schema = parseSchema(
            """
               { "type": "number", "multipleOf": 10 }
            """.trimIndent()
         )

         test("a valid multiple passes") {
            "10" shouldMatchSchema schema
         }

         test("invalid multiple fails") {
            shouldFail {
               "11" shouldMatchSchema schema
            }
         }
      }

      context("minimums") {
         val schema = parseSchema(
            """
               { "type": "number", "minimum": 0}
            """.trimIndent()
         )

         test("more than minimum passes") {
            "1" shouldMatchSchema schema
         }

         test("exactly equal to minimum passes") {
            "0" shouldMatchSchema schema
         }

         test("less than minimum fails") {
            shouldFail {
               "-1" shouldMatchSchema schema
            }.message shouldBe "$ => -1.0 should be >= 0.0"
         }
      }

      context("exclusive min") {
         val schema = parseSchema(
            """
               { "type": "number", "exclusiveMinimum": 0}
            """.trimIndent()
         )

         test("more than minimum passes") {
            "0.1" shouldMatchSchema schema
         }

         test("exactly equal to minimum fails") {
            shouldFail { "0" shouldMatchSchema schema }
               .message shouldBe "$ => 0.0 should be > 0.0"
         }

         test("less than minimum fails") {
            shouldFail {
               "-1" shouldMatchSchema schema
            }.message shouldBe "$ => -1.0 should be > 0.0"
         }
      }

      context("max") {
         val schema = parseSchema(
            """
               { "type": "number", "maximum": 5}
            """.trimIndent()
         )

         test("less than max passes") {
            "1" shouldMatchSchema schema
         }

         test("exactly equal to maximum passes") {
            "5" shouldMatchSchema schema
         }

         test("more than max fails") {
            shouldFail {
               "5.1" shouldMatchSchema schema
            }.message shouldBe "$ => 5.1 should be <= 5.0"
         }
      }

      context("exclusive max") {
         val schema = parseSchema(
            """
               { "type": "number", "exclusiveMaximum": 5}
            """.trimIndent()
         )

         test("less than max passes") {
            "4.9" shouldMatchSchema schema
         }

         test("exactly equal to max fails") {
            shouldFail { "5" shouldMatchSchema schema }
               .message shouldBe "$ => 5.0 should be < 5.0"
         }

         test("more than max fails") {
            shouldFail {
               "5.1" shouldMatchSchema schema
            }.message shouldBe "$ => 5.1 should be < 5.0"
         }
      }
   }
)
