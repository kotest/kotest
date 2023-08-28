package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class AfterTestOrderTest : DescribeSpec({

   var trail = ""

   afterSpec {
      trail shouldBe "3215421211"
   }

   afterTest {
      trail += "1"
   }

   describe("nested level 1") {
      afterTest {
         trail += "2"
      }

      it("test level 1") {
         trail += "3"
      }

      describe("nested level 2") {
         afterTest {
            trail += "4"
         }

         it("test level 2") {
            trail += "5"
         }
      }
   }
})
