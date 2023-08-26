package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

private var trail = ""

class AfterEachOrderTest : DescribeSpec({

   afterSpec {
      trail shouldBe "1452345"
   }

   afterEach {
      trail += "5"
   }

   describe("nested level 1") {
      afterEach {
         trail += "4"
      }

      it("test level 1") {
         trail += "1"
      }

      describe("nested level 2") {
         afterEach {
            trail += "3"
         }

         it("test level 2") {
            trail += "2"
         }
      }
   }
})
