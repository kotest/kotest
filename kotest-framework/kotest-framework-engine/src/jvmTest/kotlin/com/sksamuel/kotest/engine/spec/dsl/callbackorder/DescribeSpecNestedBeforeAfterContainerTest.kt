package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecNestedBeforeAfterContainerTest : DescribeSpec({

   var before = ""
   var after = ""

   afterSpec {
      before shouldBe "aegacikm"
      after shouldBe "fhjldbnb"
   }

   beforeContainer {
      before += "a"
   }

   afterContainer {
      after += "b"
   }

   describe("foo") {

      beforeContainer {
         before += "c"
      }

      afterContainer {
         after += "d"
      }

      it("b") {
         before += "e"
         after += "f"
      }

      it("e") {
         before += "g"
         after += "h"
      }

      describe("bar") {

         it("f") {
            before += "i"
            after += "j"
         }

         it("g") {
            before += "k"
            after += "l"
         }
      }

      it("h") {
         before += "m"
         after += "n"
      }
   }
})
