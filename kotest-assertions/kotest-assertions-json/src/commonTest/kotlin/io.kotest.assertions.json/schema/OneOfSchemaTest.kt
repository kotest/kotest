package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

@OptIn(ExperimentalKotest::class)
class OneOfSchemaTest : FunSpec(
   {
      context("oneOf DSL") {
         test("oneOf with string or integer - string matches") {
            val schema = jsonSchema { oneOf(string(), integer()) }
            "\"hello\"" shouldMatchSchema schema
         }

         test("oneOf with string or integer - integer matches") {
            val schema = jsonSchema { oneOf(string(), integer()) }
            "42" shouldMatchSchema schema
         }

         test("oneOf with string or integer - boolean fails (0 matches)") {
            val schema = jsonSchema { oneOf(string(), integer()) }
            "true" shouldNotMatchSchema schema
         }

         test("oneOf overlap: number and integer both match integer value (2 matches)") {
            val schema = jsonSchema { oneOf(number(), integer()) }
            // 1 matches both number() and integer() -> 2 matches -> should fail
            "1" shouldNotMatchSchema schema
         }

         test("oneOf overlap failure message indicates multiple matches") {
            val schema = jsonSchema { oneOf(number(), integer()) }
            shouldFail { "1" shouldMatchSchema schema }.message.let {
               it.shouldContain("2 matched")
               it.shouldContain("number")
               it.shouldContain("integer")
            }
         }

         test("oneOf 0 matches failure message lists violations") {
            val schema = jsonSchema { oneOf(string(), integer()) }
            shouldFail { "true" shouldMatchSchema schema }.message.let {
               it.shouldContain("oneOf")
               it.shouldContain("none matched")
            }
         }

         test("oneOf as array element") {
            val schema = jsonSchema {
               array { oneOf(string(), boolean()) }
            }
            """["foo", true, "bar", false]""" shouldMatchSchema schema
         }
      }

      context("parse oneOf from JSON Schema string") {
         test("parse oneOf schema") {
            val schema = parseSchema(
               """
               {
                  "oneOf": [
                     { "type": "string" },
                     { "type": "integer" }
                  ]
               }
               """.trimIndent()
            )
            "\"hello\"" shouldMatchSchema schema
            "42" shouldMatchSchema schema
            "true" shouldNotMatchSchema schema
         }

         test("parse oneOf overlap from JSON Schema") {
            val schema = parseSchema(
               """
               {
                  "oneOf": [
                     { "type": "number" },
                     { "type": "integer" }
                  ]
               }
               """.trimIndent()
            )
            // integer 1 matches both number and integer -> 2 matches -> fail
            "1" shouldNotMatchSchema schema
            // 3.14 matches only number -> 1 match -> pass
            "3.14" shouldMatchSchema schema
         }
      }

      context("empty schema validation") {
         test("empty oneOf throws IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> {
               jsonSchema { oneOf() }
            }.message shouldBe "oneOf requires at least one schema"
         }
      }
   }
)
