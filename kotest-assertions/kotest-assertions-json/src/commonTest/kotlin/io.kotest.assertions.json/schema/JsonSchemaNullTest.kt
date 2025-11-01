package io.kotest.assertions.json.schema

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JsonSchemaNullTest : FunSpec(
   {
      val jsonWithNulls =
         // language=JSON
         """
            {
               "prop1": null,
               "prop2": 1,
               "prop3": "string",
               "prop4": true,
               "prop5": "null",
               "prop6": "",
               "prop7": 0
            }
         """
      context("Null matchers") {
         test("null value matches null matcher") {
            jsonWithNulls shouldMatchSchema jsonSchema {
               obj {
                  `null`("prop1")
               }
            }
         }
         test("non-null value does not match null matcher") {
            shouldFail {
               jsonWithNulls shouldMatchSchema jsonSchema {
                  obj {
                     `null`("prop2")
                     `null`("prop3")
                     `null`("prop4")
                     `null`("prop5")
                     `null`("prop6")
                     `null`("prop7")
                  }
               }
            }.message shouldBe
               """
               $.prop2 => Expected null, but was number
               $.prop3 => Expected null, but was string
               $.prop4 => Expected null, but was boolean
               $.prop5 => Expected null, but was string
               $.prop6 => Expected null, but was string
               $.prop7 => Expected null, but was number
               """.trimIndent()
         }
      }
   })
