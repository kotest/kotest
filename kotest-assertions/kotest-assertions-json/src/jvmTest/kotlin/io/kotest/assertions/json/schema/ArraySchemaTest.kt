package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class ArraySchemaTest : FunSpec(
   {
      fun json(@Language("JSON") raw: String) = raw

      val intArray = jsonSchema { array { integer() } }
      val decimalArray = jsonSchema { array { decimal() } }

      val person = jsonSchema {
         obj {
            withProperty("name") { string() }
            withProperty("age") { integer() }
         }
      }

      val personArray = jsonSchema { array { person() } }

      test("Array with correct elements match") {
         """[1, 2]""" shouldMatchSchema intArray
      }

      test("Problems compound") {
         shouldFail { """[1, 2]""" shouldMatchSchema decimalArray }.message shouldBe """
            $[0] => Expected decimal, but was integer
            $[1] => Expected decimal, but was integer
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
            $[0].age => Expected integer, but was undefined
            $[2].age => Expected integer, but was undefined
         """.trimIndent()
      }
   }
)
