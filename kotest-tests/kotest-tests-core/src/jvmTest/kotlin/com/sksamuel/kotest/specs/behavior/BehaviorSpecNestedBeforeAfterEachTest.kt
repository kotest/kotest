package com.sksamuel.kotest.specs.behavior

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BehaviorSpecNestedBeforeAfterEachTest : BehaviorSpec({

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

   given("foo") {

      beforeEach {
         before += "c"
      }

      afterEach {
         after += "d"
      }

      then("b") {
         before += "e"
         after += "f"
      }

      then("e") {
         before += "g"
         after += "h"
      }

      `when`("bar") {

         then("f") {
            before += "i"
            after += "j"
         }

         then("g") {
            before += "k"
            after += "l"
         }
      }

      then("h") {
         before += "m"
         after += "n"
      }
   }
})
