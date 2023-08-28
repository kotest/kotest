package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecNestedBeforeAfterTest : DescribeSpec({

   var a = ""

   beforeSpec {
      a shouldBe ""
      a = "m"
   }

   describe("foo") {
      a shouldBe "m"
      beforeTest {
         // this should only run for the nested tests here
         a = "n"
      }
      afterTest {
         // this should only run after the nested tests
         a = "j"
      }
      it("b") {
         a shouldBe "n" // from the outer before
         a = "resetme"
      }
      it("e") {
         a shouldBe "n"
         a = "resetme"
      }

      describe("bar") {
         it("f") {
            a shouldBe "n" // from the outer before
            a = "resetme"
         }
         it("g") {
            a shouldBe "n"
            a = "resetme"
         }
      }
   }

   afterSpec {
      a shouldBe "j" // the outer most after spec should be used
   }
})
