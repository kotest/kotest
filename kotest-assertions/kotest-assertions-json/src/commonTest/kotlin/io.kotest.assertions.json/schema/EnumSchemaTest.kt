package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalKotest::class)
class EnumSchemaTest : FunSpec({

   test("string enum matches allowed values") {
      val schema = jsonSchema { enum("Avenue", "Street", "Boulevard") }
      """"Avenue"""" shouldMatchSchema schema
      """"Street"""" shouldMatchSchema schema
      """"Boulevard"""" shouldMatchSchema schema
   }

   test("string enum rejects values not in the list") {
      val schema = jsonSchema { enum("Avenue", "Street", "Boulevard") }
      shouldFail { """"Road"""" shouldMatchSchema schema }.message shouldBe """
         $ => Expected one of [Avenue, Street, Boulevard] but was string
      """.trimIndent()
   }

   test("integer enum matches allowed values") {
      val schema = jsonSchema { enum(1L, 2L, 3L) }
      "1" shouldMatchSchema schema
      "2" shouldMatchSchema schema
      "3" shouldMatchSchema schema
   }

   test("integer enum rejects unlisted values") {
      val schema = jsonSchema { enum(1L, 2L, 3L) }
      shouldFail { "4" shouldMatchSchema schema }.message shouldBe """
         $ => Expected one of [1, 2, 3] but was number
      """.trimIndent()
   }

   test("boolean enum matches allowed values") {
      val schema = jsonSchema { enum(true) }
      "true" shouldMatchSchema schema
      shouldFail { "false" shouldMatchSchema schema }.message shouldBe """
         $ => Expected one of [true] but was boolean
      """.trimIndent()
   }

   test("enum with null allows null") {
      val schema = jsonSchema { enum("hello", null) }
      """"hello"""" shouldMatchSchema schema
      "null" shouldMatchSchema schema
   }

   test("enum type mismatch") {
      val schema = jsonSchema { enum("1", "2") }
      // numeric 1 does not match string "1"
      shouldFail { "1" shouldMatchSchema schema }.message shouldBe """
         $ => Expected one of [1, 2] but was number
      """.trimIndent()
   }

   test("enum as prefixItem validates tuple position") {
      val schema = jsonSchema {
         array(
            prefixItems = listOf(number(), string(), enum("Avenue", "Street", "Boulevard"))
         )
      }
      "[1600, \"Pennsylvania Avenue NW\", \"Avenue\"]" shouldMatchSchema schema
      shouldFail {
         "[1600, \"Pennsylvania Avenue NW\", \"Road\"]" shouldMatchSchema schema
      }.message shouldBe """
         $[2] => Expected one of [Avenue, Street, Boulevard] but was string
      """.trimIndent()
   }

   test("Should parse enum schema from JSON") {
      val schema = parseSchema("""{"enum": ["red", "green", "blue"]}""")
      """"red"""" shouldMatchSchema schema
      """"green"""" shouldMatchSchema schema
      shouldFail { """"yellow"""" shouldMatchSchema schema }.message shouldBe """
         $ => Expected one of [red, green, blue] but was string
      """.trimIndent()
   }

   test("Should parse enum schema with mixed types from JSON") {
      val schema = parseSchema("""{"enum": ["red", 42, true, null]}""")
      """"red"""" shouldMatchSchema schema
      "42" shouldMatchSchema schema
      "true" shouldMatchSchema schema
      "null" shouldMatchSchema schema
      shouldFail { """"blue"""" shouldMatchSchema schema }.message shouldBe """
         $ => Expected one of [red, 42, true, null] but was string
      """.trimIndent()
   }

   test("Should parse prefixItems with enum inside from JSON") {
      val schema = parseSchema(
         """
         {
            "type": "array",
            "prefixItems": [
               {"type": "number"},
               {"enum": ["Avenue", "Street"]}
            ]
         }
         """.trimIndent()
      )
      "[1600, \"Avenue\"]" shouldMatchSchema schema
      shouldFail { "[1600, \"Road\"]" shouldMatchSchema schema }.message shouldBe """
         $[1] => Expected one of [Avenue, Street] but was string
      """.trimIndent()
   }
})
