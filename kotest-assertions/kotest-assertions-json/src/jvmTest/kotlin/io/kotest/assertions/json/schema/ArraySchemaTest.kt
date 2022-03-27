package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class ArraySchemaTest : FunSpec(
   {
      fun json(@Language("JSON") raw: String) = raw

      val numberArray = jsonSchema { array { number() } }

      val person = jsonSchema {
         obj {
            withProperty("name") { string() }
            withProperty("age") { number() }
         }
      }

      val personArray = jsonSchema { array { person() } }

      test("Array with correct elements match") {
         """[1, 2]""" shouldMatchSchema numberArray
      }

      test("Problems compound") {
         shouldFail { """["one", "two"]""" shouldMatchSchema numberArray }.message shouldBe """
            $[0] => Expected number, but was string
            $[1] => Expected number, but was string
         """.trimIndent()
      }

      test("empty array is ok") {
         "[]" shouldMatchSchema personArray
      }

      test("array with partial inner match is not ok") {
         val missingAge =
         """
            [
               { "name": "bob" },
               { "name": "bob", "age": 3 },
               { "name": "bob" }
            ]
         """.trimIndent()

         missingAge shouldNotMatchSchema personArray

         shouldFail { missingAge shouldMatchSchema personArray }.message shouldBe """
            $[0].age => Expected number, but was undefined
            $[2].age => Expected number, but was undefined
         """.trimIndent()
      }
   }
)
