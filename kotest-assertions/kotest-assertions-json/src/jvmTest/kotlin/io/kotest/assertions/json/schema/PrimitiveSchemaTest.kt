package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.beEven
import io.kotest.matchers.shouldBe

class PrimitiveMatchSchemaTest : FunSpec(
   {
      test("invalid json") {
         shouldFail {
            "[" shouldMatchSchema jsonSchema { obj() }
         }.message shouldBe """
Failed to parse actual as JSON: Expected end of the array ']', but had 'EOF' instead
JSON input: [
         """.trimIndent()
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

         test("integers match int schema") {
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

         test("String cause failure" ){
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
   }
)
