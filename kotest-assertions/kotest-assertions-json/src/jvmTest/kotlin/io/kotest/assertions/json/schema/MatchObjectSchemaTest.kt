package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language


class MatchObjectSchemaTest : FunSpec(
   {
      fun json(@Language("JSON") raw: String) = raw

      val personSchema = jsonSchema {
         obj {
            withProperty("name") { string() }
            withProperty("age") { decimal() }
         }
      }

      test("matching object passes") {
         """{ "name": "John", "age": 27.2 }""" shouldMatchSchema personSchema
      }

      test("mismatching property type causes failure") {
         shouldFail {
            json("""{ "name": "John", "age": "twentyseven" }""") shouldMatchSchema personSchema
         }.message shouldBe """
            $.age => Expected decimal but was a string
         """.trimIndent()
      }

      test("Missing schema element causes failure") {
         shouldFail {
            json("""{ "name": "John" }""") shouldMatchSchema personSchema
         }.message shouldBe """
            $.age => Expected to find decimal, but it was undefined
         """.trimIndent()
      }

      test("Problems compound") {
         shouldFail {
            json("""{ "name": 5, "age": "twentyseven" }""") shouldMatchSchema personSchema
         }.message shouldBe """
            $.name => Expected string but was an integer
            $.age => Expected decimal but was a string
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
               $.owner.name => Expected string but was an integer
            """.trimIndent()
         }
      }
   }
)
