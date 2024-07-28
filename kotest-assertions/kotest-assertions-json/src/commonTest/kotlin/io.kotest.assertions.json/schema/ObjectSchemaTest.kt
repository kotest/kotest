package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalKotest::class)
class ObjectSchemaTest : FunSpec(
   {
      val personSchemaAllowingExtraProperties = jsonSchema {
         obj {
            withProperty("name") { string() }
            withProperty("initials", optional = true) { string() }
            withProperty("age") { number() }
         }
      }

      val personSchema = parseSchema(
         """
         {
           "type": "object",
           "properties": {
             "name": { "type": "string" },
             "initials": { "type": "string" },
             "age": { "type": "number" }
           },
          "requiredProperties": [ "name", "age" ],
          "additionalProperties": false
        }
      """
      )

      test("matching object passes") {
         """{ "name": "John", "age": 27.2 }""" shouldMatchSchema personSchema
      }

      test("mismatching property type causes failure") {
         shouldFail {
            """{ "name": "John", "age": "twentyseven" }""" shouldMatchSchema personSchema
         }.message shouldBe """
            $.age => Expected number, but was string
         """.trimIndent()
      }

      test("primitive instead of object causes failure") {
         shouldFail {
            "\"hello\"" shouldMatchSchema personSchema
         }.message shouldBe """
            $ => Expected object, but was string
         """.trimIndent()
      }

      test("Extra property causes failure") {
         shouldFail {
            """{ "name": "John", "age": 27.2, "profession": "T800" }""" shouldMatchSchema personSchema
         }.message shouldBe """
            $.profession => Key undefined in schema, and schema is set to disallow extra keys
         """.trimIndent()
      }

      test("Missing required property causes failure") {
         shouldFail {
            """{ "name": "John" }""" shouldMatchSchema personSchema
         }.message shouldBe """
            $.age => Expected number, but was undefined
         """.trimIndent()
      }

      test("Extra property causes failure for scheam disallowing it") {
         shouldFail {
            """{ "name": "John", "favorite_pet": "Cat", "age": 2 }""" shouldMatchSchema personSchema
         }.message shouldBe """
            $.favorite_pet => Key undefined in schema, and schema is set to disallow extra keys
         """.trimIndent()
      }

      test("Extra property is OK when schema allows it") {
         """{ "name": "John", "favorite_pet": "Cat", "age": 2 }""" shouldMatchSchema personSchemaAllowingExtraProperties
      }

      test("Problems compound") {
         shouldFail {
            """{ "name": 5, "age": "twentyseven" }""" shouldMatchSchema personSchema
         }.message shouldBe """
            $.name => Expected string, but was number
            $.age => Expected number, but was string
         """.trimIndent()
      }

      context("nested objects") {
         val companySchema = jsonSchema {
            obj {
               withProperty("owner") { personSchema.root }
               withProperty("employees") {
                  array {
                     personSchema.root // TODO: Should be possible to compose schemas without explicitly unpacking boxing element
                  }
               }
            }
         }

         test("matching object passes") {
            """{ "owner": { "name": "Emil", "age": 34.1 }, "employees": [] }""" shouldMatchSchema
               companySchema
         }

         test("Mismatch gives good message") {
            shouldFail {
               """{ "owner": { "name": 5, "age": 34.1 }, "employees": [] }""" shouldMatchSchema
                  companySchema
            }.message shouldBe """
               $.owner.name => Expected string, but was number
            """.trimIndent()
         }
      }
   }
)
