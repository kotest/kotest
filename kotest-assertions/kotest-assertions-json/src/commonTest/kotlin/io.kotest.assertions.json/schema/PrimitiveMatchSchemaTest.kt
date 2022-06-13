package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

@OptIn(ExperimentalKotest::class)
class PrimitiveMatchSchemaTest : FunSpec(
   {
      test("invalid json") {
         shouldFail {
            "[" shouldMatchSchema jsonSchema { obj() }
         }.message shouldContain "Failed to parse actual as JSON"
      }

      context("boolean schemas") {
         val boolSchema = jsonSchema { boolean() }

         test("boolean values match bool schema") {

            "true" shouldMatchSchema boolSchema
            "false" shouldMatchSchema boolSchema
         }

         test("number mismatches") {
            shouldFail {
               "0" shouldMatchSchema boolSchema
            }.message shouldBe """
               $ => Expected boolean, but was number
            """.trimIndent()
         }
      }

      context("String schema") {
         val schema = jsonSchema { string() }

         test("string matches string schema") {
            "\"hello, world!\"" shouldMatchSchema schema
         }

         test("boolean does not match string schema") {
            shouldFail { "false" shouldMatchSchema schema }.message shouldBe """
               $ => Expected string, but was boolean
            """.trimIndent()
         }

         test("object does not match string schema") {
            shouldFail { """{ "greeting": "hello" }""" shouldMatchSchema schema }.message shouldBe """
               $ => Expected string, but was object
            """.trimIndent()
         }
      }

      context("numbers") {
         val numberSchema = jsonSchema { number() }

         test("all numbers match number schema") {
            "5" shouldMatchSchema numberSchema
            "3.14" shouldMatchSchema numberSchema
            "0" shouldMatchSchema numberSchema
            "-1" shouldMatchSchema numberSchema
         }

         test("Non-number causes failure") {
            shouldFail { "false" shouldMatchSchema numberSchema }.message shouldBe """
               $ => Expected number, but was boolean
            """.trimIndent()
         }

         test("String cause failure") {
            shouldFail {
               "\"5\"" shouldMatchSchema numberSchema
            }.message shouldBe """
               $ => Expected number, but was string
            """.trimIndent()
         }

         test("negated assertion works") {
            "false" shouldNotMatchSchema numberSchema
         }
      }

      context("integers") {
         val intSchema = jsonSchema { integer() }

         test("integers match int schema") {
            "5" shouldMatchSchema intSchema
            "0" shouldMatchSchema intSchema
            "-1" shouldMatchSchema intSchema
         }

         test("decimals cause failures") {
            shouldFail { "5.2" shouldMatchSchema intSchema }.message shouldBe """
               $ => Expected integer, but was number
            """.trimIndent()
         }

         test("Non-number causes failure") {
            shouldFail { "false" shouldMatchSchema intSchema }.message shouldBe """
               $ => Expected integer, but was boolean
            """.trimIndent()
         }

         test("String cause failure") {
            shouldFail {
               "\"5\"" shouldMatchSchema intSchema
            }.message shouldBe """
               $ => Expected integer, but was string
            """.trimIndent()
         }

         test("negated assertion works") {
            "false" shouldNotMatchSchema intSchema
         }
      }
   }
)
