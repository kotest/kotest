package io.kotest.assertions.json

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class PrettyPrintTest : FunSpec(
   {
      test("print object") {
         shouldFail {
            "{}" shouldEqualJson """ { "a": { "b": "c", "x": 1 } }"""
         }.message shouldContain """
            {
              "a": {
                "b": "c",
                "x": 1
              }
            }
         """.trimIndent()
      }

      test("print string with inner quotes") {
         shouldFail {
            "{}" shouldEqualJson """{ "a": "\"x\"" }"""
         }.message shouldContain """
            {
              "a": "\"x\""
            }
         """.trimIndent()
      }

      test("print array") {
         shouldFail {
            "{}" shouldEqualJson """[ { "x": 1 }, { "y": [2, 3] }, { "point": { "x": 1.0, "y": 2.0, "z": null } } ]"""
         }.message shouldContain """
            [
              {
                "x": 1
              },
              {
                "y": [
                  2,
                  3
                ]
              },
              {
                "point": {
                  "x": 1.0,
                  "y": 2.0,
                  "z": null
                }
              }
            ]
         """.trimIndent()
      }

      test("print empty array and object") {
         shouldFail {
            "{}" shouldEqualJson """ { "a": {}, "b": [] } """
         }.message shouldContain """
            {
              "a": {},
              "b": []
            }
         """.trimIndent()
      }
   }
)
