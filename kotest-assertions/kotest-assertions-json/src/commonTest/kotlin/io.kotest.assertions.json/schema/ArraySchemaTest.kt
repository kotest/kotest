package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ArraySchemaTest : FunSpec(
   {
      val numberArray = jsonSchema { array { number() } }

      val person = jsonSchema {
         obj {
            withProperty("name", required = true) { string() }
            withProperty("age", required = true) { number() }
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

      test("Array not contains string") {
         val array = "[1,1]"
         val containsStringArray = jsonSchema {
            array(contains = containsSpec { string() })
         }
         "[\"bob\"]" shouldMatchSchema  containsStringArray
         shouldFail { array shouldMatchSchema containsStringArray }.message shouldBe """
            $ => Expected any item of type string
            $.contains[0] => Expected string, but was number
            $.contains[1] => Expected string, but was number
         """.trimIndent()
      }

      test("Should parse schema with contains") {
         val schema = parseSchema(
            """
               { "type": "array", "contains": {"elementType": {"type": "number"} } }
            """.trimIndent()
         )
         shouldFail { "[\"bob\"]" shouldMatchSchema schema }.message shouldBe """
            $ => Expected any item of type number
            $.contains[0] => Expected number, but was string
         """.trimIndent()
      }

      test("Should parse schema with non primitive contains") {
         val schema = parseSchema(
            """
               { "type": "array", "contains": {"elementType" : {"type": "object", "properties": { "name": { "type": "string" }}}}}
            """.trimIndent()
         )
         shouldFail { "[\"bob\"]" shouldMatchSchema schema }.message shouldBe """
            $ => Expected any item of type object
            $.contains[0] => Expected object, but was string
         """.trimIndent()
         "[\"life\", \"universe\", \"everything\", {\"name\": \"bob\"}]" shouldMatchSchema schema
      }

      test("Array contains strings and numbers") {
         val array = "[\"life\", \"universe\", \"everything\", 42]"
         val containsStringArray = jsonSchema {
            array(contains = containsSpec { number() })
         }
         array shouldMatchSchema containsStringArray
      }

      test("Array not contains person") {
         val array = "[\"life\", 42]"
         val containsPersonArray = jsonSchema {
            array(contains = containsSpec { person() })
         }
         shouldFail { array shouldMatchSchema containsPersonArray }.message shouldBe """
            $ => Expected any item of type object
            $.contains[0] => Expected object, but was string
            $.contains[1] => Expected object, but was number
         """.trimIndent()
      }

      test("Array contains person with wrong age type") {
         val array = "[{\"name\": \"bob\", \"age\": \"wrong\"}]"
         val containsPersonArray = jsonSchema {
            array(contains = containsSpec { person() })
         }
         shouldFail { array shouldMatchSchema containsPersonArray }.message shouldBe """
            $ => Expected any item of type object
            $.contains[0].age => Expected number, but was string
         """.trimIndent()
      }

      test("Array without contains and elementType") {
         val array = jsonSchema {
            array()
         }
         "[1, \"bob\"]" shouldMatchSchema array
      }
   }
)
