package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecNestedBeforeAfterTest : FunSpec({

   var a = ""

   beforeSpec {
      a shouldBe ""
      a = "webble"
   }

   context("foo") {
      a shouldBe "webble"
      beforeTest {
         // this should only run for the nested tests here
         a = "wibble"
      }
      afterTest {
         // this should only run after the nested tests
         a = "wabble"
      }
      test("a") {
         a shouldBe "wibble"
      }
      test("b") {
         a shouldBe "wibble"
      }
   }

   context("bar") {
      a shouldBe "wabble"
      beforeTest {
         // this should only run for the nested tests here
         a = "wobble"
      }
      afterTest {
         // this should only run after the nested tests
         a = "wubble"
      }
      test("a") {
         a shouldBe "wobble"
      }
      test("b") {
         a shouldBe "wobble"
      }

      context("fizz") {
         test("a") {
            a shouldBe "wobble" // from the outer before
            a = "resetme"
         }
         test("b") {
            a shouldBe "wobble"
            a = "resetme"
         }
      }
   }

   afterSpec {
      a shouldBe "wubble"
   }
})
