package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

private var trail = ""

class AfterTestOrderTest : DescribeSpec({

   afterSpec {
      trail shouldBe "1452345455"
   }

   afterTest {
      trail += "5"
   }

   describe("nested level 1") {
      afterTest {
         trail += "4"
      }

      it("test level 1") {
         trail += "1"
      }

      describe("nested level 2") {
         afterTest {
            trail += "3"
         }

         it("test level 2") {
            trail += "2"
         }
      }
   }
})
