package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BehaviorSpecNestedBeforeAfterTest : BehaviorSpec({

   var a = ""

   beforeSpec {
      a shouldBe ""
      a = "m"
   }

   given("foo") {
      a shouldBe "m"
      beforeTest {
         // this should only run for the nested tests here
         a = "n"
      }
      afterTest {
         // this should only run after the nested tests
         a = "j"
      }
      `when`("b") {
         a shouldBe "n" // from the outer before
         a = "resetme"
         beforeTest {
            a = "d"
         }
         then("c") {
            a shouldBe "d" // from the immediate before
            a = "resetme"
         }
         then("d") {
            a shouldBe "d"  // from the immediate before
            a = "resetme"
         }
      }
      `when`("e") {
         a shouldBe "n"
         a = "resetme"
         beforeTest {
            a = "e"
         }
         afterTest {
            a = "z" // this should never be used as the outer scope afters should also run
         }
         then("f") {
            a shouldBe "e"
            a = "resetme"
         }
         then("g") {
            a shouldBe "e"
            a = "resetme"
         }
      }
      `when`("h") {
         then("i") {
            a shouldBe "n" // from the outer before
            a = "resetme"
         }
         then("k") {
            a shouldBe "n"
            a = "resetme"
         }
      }
   }

   afterSpec {
      a shouldBe "j" // the outer most after spec should be used
   }
})
