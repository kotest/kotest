package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
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
                "first_name": { "type": "string" },
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

         test("correctly parses a proper JSON schema") {
            schema shouldBe jsonSchema {
               jsonObject {
                  withProperty("first_name") { string() }
                  withProperty("last_name") { string() }
                  withProperty("birthday") { string() } // TODO: Once matchers are implemented, this node should have some sort of date format matcher
                  withProperty("address") {
                     jsonObject {
                        withProperty("street_address") { string() }
                        withProperty("city") { string() }
                        withProperty("state") { string() }
                        withProperty("country") { string() }
                     }
                  }
               }
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
${'$'}.name => Key undefined in schema, and schema is set to disallow extra keys
${'$'}.address => Expected object, but was string
${'$'}.first_name => Expected string, but was undefined
${'$'}.last_name => Expected string, but was undefined
${'$'}.address.street_address => Expected string, but was undefined
${'$'}.address.city => Expected string, but was undefined
${'$'}.address.state => Expected string, but was undefined
${'$'}.address.country => Expected string, but was undefined
            """.trimIndent()
         }
      }
   }
)
