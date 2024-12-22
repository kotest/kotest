package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ParseSchemaTest : FunSpec(
   {

      test("primitive schema parsing") {
         parseSchema("""{ "type": "string" }""") shouldBe jsonSchema {
            string()
         }
      }

      context("sample schema") {
         // Sample from https://json-schema.org/understanding-json-schema/about.html
         val schema = parseSchema(
            """{
              "type": "object",
              "properties": {
                "first_name": {
                   "type": "string",
                   "minLength": 3,
                   "maxLength": 10,
                   "pattern": "[A-Z][a-z]+"
                 },
                "last_name": { "type": "string" },
                "birthday": { "type": "string", "format": "date" },
                "address": {
                  "type": "object",
                  "properties": {
                    "street_address": { "type": "string" },
                    "city": { "type": "string" },
                    "state": { "type": "string" },
                    "country": { "type" : "string" }
                  }
                }
              }
            }"""
         )

         context("String constraints are applied") {
            withData(
               "e" to """"e" should have minimum length of 3""",
               "george" to """"george" should match regex [A-Z][a-z]+""",
               "Helmut-Alexander" to """"Helmut-Alexander" should have maximum length of 10""",
            ) { (name, expectedMessage) ->
               shouldFail {
                  """
                  {
                    "first_name": "$name",
                    "last_name": "Washington",
                    "birthday": "1732-02-22",
                    "address": {
                      "street_address": "3200 Mount Vernon Memorial Highway",
                      "city": "Mount Vernon",
                      "state": "Virginia",
                      "country": "United States"
                    }
                  }
                  """.trimIndent() shouldMatchSchema schema
               }.message shouldBe """
                  $.first_name => $expectedMessage
               """.trimIndent()
            }
         }

         test("Match against sample schema") {
            """
               {
                 "first_name": "George",
                 "last_name": "Washington",
                 "birthday": "1732-02-22",
                 "address": {
                   "street_address": "3200 Mount Vernon Memorial Highway",
                   "city": "Mount Vernon",
                   "state": "Virginia",
                   "country": "United States"
                 }
               }
            """.trimIndent() shouldMatchSchema schema
         }

         test("mismatch against sample schema") {
            val mismatchingSample =
               """
               {
                 "name": "George Washington",
                 "birthday": "February 22, 1732",
                 "address": "Mount Vernon, Virginia, United States"
               }
               """.trimIndent()

            mismatchingSample shouldNotMatchSchema schema

            shouldFail { mismatchingSample shouldMatchSchema schema }.message shouldBe """
               $.address => Expected object, but was string
            """.trimIndent()
         }
      }
   }
)
