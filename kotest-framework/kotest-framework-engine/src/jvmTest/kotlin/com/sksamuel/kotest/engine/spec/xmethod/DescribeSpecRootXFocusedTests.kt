package com.sksamuel.kotest.engine.spec.xmethod

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecRootXFocusedTests : DescribeSpec() {
   init {

      var tests = 0

      afterSpec {
         tests shouldBe 1
      }

      it("root test without config") {
         error("boom") // will be ignored because of a focused test
      }

      fit("focused root test without config") {
         tests++
      }

      xit("disabled root test without config") {
         error("boom") // will be ignored because of xmethod
      }
   }
}
