package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

private var trail = ""

class AfterAnyOrderTest : DescribeSpec({

   afterSpec {
      trail shouldBe "1452345455"
   }

   afterAny {
      trail += "5"
   }

   describe("nested level 1") {
      afterAny {
         trail += "4"
      }

      it("test level 1") {
         trail += "1"
      }

      describe("nested level 2") {
         afterAny {
            trail += "3"
         }

         it("test level 2") {
            trail += "2"
         }
      }
   }
})
