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
      val personArray = jsonSchema {
         array {
            obj {
               withProperty("name") { string() }
               withProperty("age") { integer() }
            }
         }
      }

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
         """
            [ { "name": "bob" } ]
         """.trimIndent() shouldNotMatchSchema personArray
      }
   }
)
