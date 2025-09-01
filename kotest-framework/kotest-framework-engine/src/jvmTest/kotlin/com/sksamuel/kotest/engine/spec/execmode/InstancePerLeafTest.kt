package com.sksamuel.kotest.engine.spec.execmode

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

private var trace = ""

class InstancePerLeafTest : DescribeSpec({

   isolationMode = IsolationMode.InstancePerLeaf

   afterSpec {
      trace shouldBe "d1_c1_i_d1_c2_i2_"
   }

   describe("d1") {
      trace += "d1_"

      context("c1") {
         trace += "c1_"

         it("i1") {
            trace += "i1_"
         }
      }

      context("c2") {
         trace += "c2_"

         it("i2") {
            trace += "i2_"
         }
      }
   }
})
