package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

@OptIn(ExperimentalKotest::class)
class AnyOfOneOfSchemaTest : FunSpec(
   {
      context("anyOf DSL") {
         test("anyOf with string or integer - string matches") {
            val schema = jsonSchema { anyOf(string(), integer()) }
            "\"hello\"" shouldMatchSchema schema
         }

         test("anyOf with string or integer - integer matches") {
            val schema = jsonSchema { anyOf(string(), integer()) }
            "42" shouldMatchSchema schema
         }

         test("anyOf with string or integer - boolean fails") {
            val schema = jsonSchema { anyOf(string(), integer()) }
            "true" shouldNotMatchSchema schema
         }

         test("anyOf failure message lists all candidates with paths") {
            val schema = jsonSchema { anyOf(string(), integer()) }
            shouldFail { "true" shouldMatchSchema schema }.message.let {
               it.shouldContain("anyOf")
               it.shouldContain("string")
               it.shouldContain("integer")
               it.shouldContain("$")
            }
         }

         test("anyOf failure message includes nested path for object candidates") {
            val schema = jsonSchema {
               obj {
                  withProperty("value") {
                     anyOf(
                        string(),
                        obj { withProperty("name") { string() } }
                     )
                  }
               }
            }
            shouldFail {
               """{"value": 42}""" shouldMatchSchema schema
            }.message.let {
               it.shouldContain("$.value")
            }
         }

         test("anyOf as array element type") {
            val schema = jsonSchema {
               array { anyOf(string(), integer()) }
            }
            """["foo", 1, "bar", 2]""" shouldMatchSchema schema
            shouldFail { """["foo", true]""" shouldMatchSchema schema }
         }

         test("anyOf as object property") {
            val schema = jsonSchema {
               obj {
                  withProperty("value") { anyOf(string(), integer()) }
               }
            }
            """{"value": "hello"}""" shouldMatchSchema schema
            """{"value": 42}""" shouldMatchSchema schema
            """{"value": true}""" shouldNotMatchSchema schema
         }

         test("anyOf with object schemas") {
            val schema = jsonSchema {
               anyOf(
                  obj {
                     withProperty("name") { string() }
                  },
                  obj {
                     withProperty("id") { integer() }
                  }
               )
            }
            """{"name": "alice"}""" shouldMatchSchema schema
            """{"id": 1}""" shouldMatchSchema schema
            """{"other": true}""" shouldNotMatchSchema schema
         }
      }

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

      context("nested anyOf/oneOf") {
         test("anyOf inside oneOf") {
            val schema = jsonSchema {
               oneOf(
                  anyOf(string(), integer()),
                  boolean()
               )
            }
            "\"hello\"" shouldMatchSchema schema
            "42" shouldMatchSchema schema
            "true" shouldMatchSchema schema
            "null" shouldNotMatchSchema schema
         }

         test("anyOf with nested object and primitive") {
            val schema = jsonSchema {
               anyOf(
                  string(),
                  obj {
                     withProperty("name") { string() }
                  }
               )
            }
            "\"simple\"" shouldMatchSchema schema
            """{"name": "alice"}""" shouldMatchSchema schema
            "42" shouldNotMatchSchema schema
         }
      }

      context("parse anyOf/oneOf from JSON Schema string") {
         test("parse anyOf schema") {
            val schema = parseSchema(
               """
               {
                  "anyOf": [
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

         test("parse nested anyOf in object property") {
            val schema = parseSchema(
               """
               {
                  "type": "object",
                  "properties": {
                     "value": {
                        "anyOf": [
                           { "type": "string" },
                           { "type": "number" }
                        ]
                     }
                  }
               }
               """.trimIndent()
            )
            """{"value": "hello"}""" shouldMatchSchema schema
            """{"value": 3.14}""" shouldMatchSchema schema
            """{"value": true}""" shouldNotMatchSchema schema
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
         test("empty anyOf throws IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> {
               jsonSchema { anyOf() }
            }.message shouldBe "anyOf requires at least one schema"
         }

         test("empty oneOf throws IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> {
               jsonSchema { oneOf() }
            }.message shouldBe "oneOf requires at least one schema"
         }
      }
   }
)
