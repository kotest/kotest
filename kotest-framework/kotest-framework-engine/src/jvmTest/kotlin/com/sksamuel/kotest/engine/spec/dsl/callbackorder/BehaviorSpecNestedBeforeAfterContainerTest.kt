package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BehaviorSpecNestedBeforeAfterContainerTest : BehaviorSpec({

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

   given("foo") {

      beforeContainer {
         before += "c"
      }

      afterContainer {
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
