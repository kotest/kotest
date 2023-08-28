package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecNestedBeforeAfterContainerTest : FunSpec({

   var before = ""
   var after = ""

   afterSpec {
      before shouldBe "aegackmo"
      after shouldBe "fhlndbpb"
   }

   beforeContainer {
      before += "a"
   }

   afterContainer {
      after += "b"
   }

   context("foo") {

      beforeContainer {
         before += "c"
      }

      afterContainer {
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

         beforeContainer {
            error("should never be invoked as no further nested containers")
         }

         afterContainer {
            error("should never be invoked as no further nested containers")
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
