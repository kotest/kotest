package com.sksamuel.kotest.engine.spec.isolation

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

/**
 * A test to demonstrate that in InstancePerTest mode, tests with the same name do not get squashed into one test only.
 */
class InstancePerTestTest1 : DescribeSpec({

   isolationMode = IsolationMode.InstancePerTest

   describe("describe") {
      it("tests with the same name") {
         1 + 1 shouldBe 2
      }
      it("tests with the same name") {
         1 + 1 shouldBe 2
      }
   }
})
