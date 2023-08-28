package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecNestedBeforeAfterEachTest : FunSpec({

   var before = ""
   var after = ""

   afterSpec {
      before shouldBe "aceacgacikacimaco"
      after shouldBe "fdbhdbldbjndbjdbpdbb"
   }

   beforeEach {
      before += "a"
   }

   afterTest {
      after += "b"
   }

   context("foo") {

      beforeEach {
         before += "c"
      }

      afterAny {
         after += "d"
      }

      test("b") {
         before += "e"
         after += "f"
      }

      test("e") {
         before += "g"
         after += "h"
      }

      context("bar") {

         beforeEach {
            before += "i"
         }

         afterEach {
            after += "j"
         }

         test("f") {
            before += "k"
            after += "l"
         }

         test("g") {
            before += "m"
            after += "n"
         }
      }

      test("h") {
         before += "o"
         after += "p"
      }
   }
})
