package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

@OptIn(ExperimentalKotest::class)
class AnyOfSchemaTest : FunSpec(
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

      context("nested combinators") {
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

      context("parse anyOf from JSON Schema string") {
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
      }

      context("empty schema validation") {
         test("empty anyOf throws IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> {
               jsonSchema { anyOf() }
            }.message shouldBe "anyOf requires at least one schema"
         }
      }
   }
)
