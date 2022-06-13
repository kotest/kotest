package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalKotest::class)
class ArraySchemaTest : FunSpec(
   {
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

      test("Should parse schema with min,max values") {
         val schema = parseSchema(
            """
               { "type": "array", "minItems": 2, "maxItems": 3, "elementType": {"type": "number"} }
            """.trimIndent()
         )
         "[1]" shouldNotMatchSchema schema
      }

      test("Array size smaller than minItems") {
         val array = "[1]"
         val sizeBoundedArray = jsonSchema {
            array(minItems = 2, maxItems = 3) { number() }
         }
         array shouldNotMatchSchema sizeBoundedArray
         shouldFail { array shouldMatchSchema sizeBoundedArray }.message shouldBe """
            $ => Expected items between 2 and 3, but was 1
         """.trimIndent()
      }

      test("Array size larger than maxItems") {
         val array = "[1,2]"
         val sizeBoundedArray = jsonSchema {
            array(minItems = 0, maxItems = 1) { number() }
         }
         array shouldNotMatchSchema sizeBoundedArray
         shouldFail { array shouldMatchSchema sizeBoundedArray }.message shouldBe """
            $ => Expected items between 0 and 1, but was 2
         """.trimIndent()
      }

      test("Should parse schema with matcher unique") {
         val schema = parseSchema(
            """
               { "type": "array", "uniqueItems": true, "elementType": {"type": "number"} }
            """.trimIndent()
         )
         "[1,1]" shouldNotMatchSchema schema
      }

      test("Array not unique") {
         val array = "[1,1]"
         val uniqueArray = jsonSchema {
            array(uniqueItems = true) { number() }
         }
         array shouldNotMatchSchema uniqueArray
         shouldFail { array shouldMatchSchema uniqueArray }.message shouldBe """
            $ => Sequence should be Unique
         """.trimIndent()
      }
   }
)
