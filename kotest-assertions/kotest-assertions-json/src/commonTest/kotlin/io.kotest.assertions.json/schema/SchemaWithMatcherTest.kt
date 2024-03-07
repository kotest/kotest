package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.and
import io.kotest.matchers.doubles.beGreaterThanOrEqualTo
import io.kotest.matchers.doubles.beLessThanOrEqualTo
import io.kotest.matchers.doubles.beMultipleOf
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.haveMaxLength
import io.kotest.matchers.string.haveMinLength

@OptIn(ExperimentalKotest::class)
class SchemaWithMatcherTest : FunSpec(
   {
      test("Even numbers") {
         val evenNumbers = jsonSchema { number { beMultipleOf(2.0) } }

         "2" shouldMatchSchema evenNumbers

         shouldFail { "3" shouldMatchSchema evenNumbers }
            .message shouldBe """
               $ => 3.0 should be multiple of 2.0
         """.trimIndent()
      }

      context("smoke") {
         val schema = jsonSchema {
            array {
               obj {
                  withProperty("name") { string { haveMinLength(3) and haveMaxLength(20) } }
                  withProperty("age") { number { beGreaterThanOrEqualTo(0.0) and beLessThanOrEqualTo(120.0) } }
               }
            }
         }

         test("Violating schema") {
            shouldFail {
               // language=JSON
               """
                  [
                    {
                      "name": "bo",
                      "age": 92
                    },
                    {
                      "name": "sophie",
                      "age": 33
                    },
                    {
                      "name": "joe",
                      "age": -1
                    },
                    {
                      "name": "alexander the extremely great",
                      "age": 12
                    }
                  ]
               """ shouldMatchSchema schema
            }.message shouldBe """
               $[0].name => "bo" should have minimum length of 3
               $[2].age => -1.0 should be >= 0.0
               $[3].name => "alexander the extremely great" should have maximum length of 20
            """.trimIndent()
         }
      }
   }
)
