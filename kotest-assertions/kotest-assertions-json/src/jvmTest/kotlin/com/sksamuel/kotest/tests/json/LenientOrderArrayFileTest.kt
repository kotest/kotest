package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.ArrayOrder
import io.kotest.assertions.json.file.shouldEqualJson
import io.kotest.assertions.json.paths.shouldEqualJson
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import java.io.File
import java.nio.file.Path

class LenientOrderArrayFileTest : FunSpec({
   infix fun File.shouldEqualJsonIgnoringOrder(other: String) = this shouldEqualJson {
      arrayOrder = ArrayOrder.Lenient
      other
   }
   infix fun Path.shouldEqualJsonIgnoringOrder(other: String) = this shouldEqualJson {
      arrayOrder = ArrayOrder.Lenient
      other
   }

   test("simple") {
      withJsonTestFile("[1, 2]") shouldEqualJsonIgnoringOrder "[2, 1]"
      withJsonTestFile("[1, 2]").toPath() shouldEqualJsonIgnoringOrder "[2, 1]"
   }

   test("multiple copies") {
      withJsonTestFile("[1, 2, 2]") shouldEqualJsonIgnoringOrder "[2, 1, 2]"
      withJsonTestFile("[1, 2, 2]").toPath() shouldEqualJsonIgnoringOrder "[2, 1, 2]"
   }

   test("duplicates in actual") {
      shouldFail {
         withJsonTestFile("[1, 2, 2]") shouldEqualJsonIgnoringOrder "[1, 2, 0]"
      }.shouldHaveMessage(
         """
            At '[2]' has extra element '2' not found (or too few) in '[1,2,0]'

            expected:<[
              1,
              2,
              0
            ]> but was:<[
              1,
              2,
              2
            ]>
         """.trimIndent()
      )

      shouldFail {
         withJsonTestFile("[1, 2, 2]").toPath() shouldEqualJsonIgnoringOrder "[1, 2, 0]"
      }.shouldHaveMessage(
         """
            At '[2]' has extra element '2' not found (or too few) in '[1,2,0]'

            expected:<[
              1,
              2,
              0
            ]> but was:<[
              1,
              2,
              2
            ]>
         """.trimIndent()
      )
   }

   test("array with objects in different order") {
      val a = """
         {
             "someList": [
                 {
                     "type": "SOME_TYPE_2"
                 },
                 {
                     "type": "SOME_TYPE_1"
                 }
             ]
         }
      """.trimIndent()

      val b = """
         {
             "someList": [
                 {
                     "type": "SOME_TYPE_1"
                 },
                 {
                     "type": "SOME_TYPE_2"
                 }
             ]
         }
      """.trimIndent()

      withJsonTestFile(a) shouldEqualJsonIgnoringOrder b
      withJsonTestFile(a).toPath() shouldEqualJsonIgnoringOrder b
   }

   test("array of objects - missing object") {
      val a = """
         {
             "someList": [
                 {
                     "type": "SOME_TYPE_2"
                 },
                 {
                     "type": "SOME_TYPE_1"
                 },
                 {
                     "type": "SOME_TYPE_3"
                 }
             ]
         }
      """.trimIndent()

      val b = """
         {
             "someList": [
                 {
                     "type": "SOME_TYPE_1"
                 },
                 {
                     "type": "SOME_TYPE_2"
                 },
                 {
                     "type": "SOME_TYPE_2"
                 }
             ]
         }
      """.trimIndent()

      shouldFail {
         withJsonTestFile(a) shouldEqualJsonIgnoringOrder b
      }.shouldHaveMessage(
         """
            At 'someList.[2]' has extra element '{"type": SOME_TYPE_3}' not found (or too few) in '[{"type": SOME_TYPE_1},{"type": SOME_TYPE_2},{"type": SOME_TYPE_2}]'

            expected:<{
              "someList": [
                {
                  "type": "SOME_TYPE_1"
                },
                {
                  "type": "SOME_TYPE_2"
                },
                {
                  "type": "SOME_TYPE_2"
                }
              ]
            }> but was:<{
              "someList": [
                {
                  "type": "SOME_TYPE_2"
                },
                {
                  "type": "SOME_TYPE_1"
                },
                {
                  "type": "SOME_TYPE_3"
                }
              ]
            }>
         """.trimIndent()
      )

      shouldFail {
         withJsonTestFile(a).toPath() shouldEqualJsonIgnoringOrder b
      }.shouldHaveMessage(
         """
            At 'someList.[2]' has extra element '{"type": SOME_TYPE_3}' not found (or too few) in '[{"type": SOME_TYPE_1},{"type": SOME_TYPE_2},{"type": SOME_TYPE_2}]'

            expected:<{
              "someList": [
                {
                  "type": "SOME_TYPE_1"
                },
                {
                  "type": "SOME_TYPE_2"
                },
                {
                  "type": "SOME_TYPE_2"
                }
              ]
            }> but was:<{
              "someList": [
                {
                  "type": "SOME_TYPE_2"
                },
                {
                  "type": "SOME_TYPE_1"
                },
                {
                  "type": "SOME_TYPE_3"
                }
              ]
            }>
         """.trimIndent()
      )
   }
})
