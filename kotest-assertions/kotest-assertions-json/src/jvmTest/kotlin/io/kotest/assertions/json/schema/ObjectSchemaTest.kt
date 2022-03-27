package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language


class ObjectSchemaTest : FunSpec(
   {
      fun json(@Language("JSON") raw: String) = raw

      val personSchema = jsonSchema {
         obj {
            withProperty("name") { string() }
            withProperty("age") { number() }
         }
      }

      test("matching object passes") {
         """{ "name": "John", "age": 27.2 }""" shouldMatchSchema personSchema
      }

      test("mismatching property type causes failure") {
         shouldFail {
            json("""{ "name": "John", "age": "twentyseven" }""") shouldMatchSchema personSchema
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
            json("""{ "name": "John", "age": 27.2, "profession": "T800" }""") shouldMatchSchema personSchema
         }.message shouldBe """
            $.profession => Key undefined in schema, and schema is set to disallow extra keys
         """.trimIndent()
      }

      test("Property undefined in schema causes failure") {
         shouldFail {
            json("""{ "name": "John" }""") shouldMatchSchema personSchema
         }.message shouldBe """
            $.age => Expected number, but was undefined
         """.trimIndent()
      }

      test("Problems compound") {
         shouldFail {
            json("""{ "name": 5, "age": "twentyseven" }""") shouldMatchSchema personSchema
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
            json("""{ "owner": { "name": "Emil", "age": 34.1 }, "employees": [] }""") shouldMatchSchema
               companySchema
         }

         test("Mismatch gives good message") {
            shouldFail {
               json("""{ "owner": { "name": 5, "age": 34.1 }, "employees": [] }""") shouldMatchSchema
                  companySchema
            }.message shouldBe """
               $.owner.name => Expected string, but was number
            """.trimIndent()
         }
      }
   }
)
