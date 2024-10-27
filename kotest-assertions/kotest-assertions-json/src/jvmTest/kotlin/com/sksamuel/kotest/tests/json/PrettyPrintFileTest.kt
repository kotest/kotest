package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.file.shouldEqualJson
import io.kotest.assertions.json.paths.shouldEqualJson
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class PrettyPrintFileTest : FunSpec({
   test("print object") {
      shouldFail {
         withJsonTestFile("{}") shouldEqualJson """ { "a": { "b": "c", "x": 1 } }"""
      }.message shouldContain """
         {
           "a": {
             "b": "c",
             "x": 1
           }
         }
      """.trimIndent()

      shouldFail {
         withJsonTestFile("{}").toPath() shouldEqualJson """ { "a": { "b": "c", "x": 1 } }"""
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
         withJsonTestFile("{}") shouldEqualJson """{ "a": "\"x\"" }"""
      }.message shouldContain """
         {
           "a": "\"x\""
         }
      """.trimIndent()

      shouldFail {
         withJsonTestFile("{}").toPath() shouldEqualJson """{ "a": "\"x\"" }"""
      }.message shouldContain """
         {
           "a": "\"x\""
         }
      """.trimIndent()
   }

   test("print array") {
      shouldFail {
         withJsonTestFile("{}") shouldEqualJson """[ { "x": 1 }, { "y": [2, 3] }, { "point": { "x": 1.0, "y": 2.0, "z": null } } ]"""
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

      shouldFail {
         withJsonTestFile("{}").toPath() shouldEqualJson """[ { "x": 1 }, { "y": [2, 3] }, { "point": { "x": 1.0, "y": 2.0, "z": null } } ]"""
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
         withJsonTestFile("{}") shouldEqualJson """ { "a": {}, "b": [] } """
      }.message shouldContain """
         {
           "a": {},
           "b": []
         }
      """.trimIndent()

      shouldFail {
         withJsonTestFile("{}").toPath() shouldEqualJson """ { "a": {}, "b": [] } """
      }.message shouldContain """
         {
           "a": {},
           "b": []
         }
      """.trimIndent()
   }
})
