package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.and
import io.kotest.matchers.ints.beEven
import io.kotest.matchers.ints.beInRange
import io.kotest.matchers.longs.beInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.haveMaxLength
import io.kotest.matchers.string.haveMinLength
import io.kotest.matchers.types.beInstanceOf

class SchemaWithMatcherTest : FunSpec(
   {
      test("Even numbers") {
         val evenNumbers = jsonSchema { number { beInstanceOf<Int>() and beEven().contramap { it as Int } } }

         "2" shouldMatchSchema evenNumbers

         shouldFail { "3" shouldMatchSchema evenNumbers }
            .message shouldBe """
               $ => 3 should be even
            """.trimIndent()
      }

      context("smoke") {
         val schema = jsonSchema {
            array {
               obj {
                  withProperty("name") { string { haveMinLength(3) and haveMaxLength(20) } }
                  withProperty("age") { number { beInRange(0..120).contramap { it as Int } } }
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
               $[2].age => -1 should be in range 0..120
               $[3].name => "alexander the extremely great" should have maximum length of 20
            """.trimIndent()
         }
      }
   }
)
