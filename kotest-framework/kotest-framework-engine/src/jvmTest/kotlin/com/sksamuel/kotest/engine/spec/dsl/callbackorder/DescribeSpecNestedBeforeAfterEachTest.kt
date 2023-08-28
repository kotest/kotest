package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecNestedBeforeAfterEachTest : DescribeSpec({

   var before = ""
   var after = ""

   afterSpec {
      before shouldBe "aceacgaciackacm"
      after shouldBe "fdbhdbjdbldbndb"
   }

   beforeEach {
      before += "a"
   }

   afterEach {
      after += "b"
   }

   describe("foo") {

      beforeEach {
         before += "c"
      }

      afterEach {
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
