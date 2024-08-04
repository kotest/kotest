package io.kotest.assertions.json

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.throwable.shouldHaveMessage

class LenientOrderArrayTest : FunSpec(
   {
      infix fun String.shouldEqualJsonIgnoringOrder(other: String) =
         @Suppress("DEPRECATION")
         this.shouldEqualJson(other, compareJsonOptions { arrayOrder = ArrayOrder.Lenient })

      test("simple") {
         "[1, 2]" shouldEqualJsonIgnoringOrder "[2, 1]"
      }

      test("multiple copies") {
         "[1, 2, 2]" shouldEqualJsonIgnoringOrder "[2, 1, 2]"
      }

      test("duplicates in actual") {
         shouldFail {
            "[1, 2, 2]" shouldEqualJsonIgnoringOrder "[1, 2, 0]"
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
         """

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
         """

         a shouldEqualJsonIgnoringOrder b
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
         """

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
         """

         shouldFail {
            a shouldEqualJsonIgnoringOrder b
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
   }
)
