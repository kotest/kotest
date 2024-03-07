package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

private var trail = ""

class AfterContainerOrderTest : DescribeSpec({

   afterSpec {
      trail shouldBe "12455"
   }

   afterContainer {
      trail += "5"
   }

   describe("nested level 1") {
      afterContainer {
         trail += "4"
      }

      it("test level 1") {
         trail += "1"
      }

      describe("nested level 2") {
         afterContainer {
            trail += "3"
         }

         it("test level 2") {
            trail += "2"
         }
      }
   }
})
