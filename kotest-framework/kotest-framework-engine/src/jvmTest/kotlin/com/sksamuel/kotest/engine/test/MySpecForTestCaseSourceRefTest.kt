package com.sksamuel.kotest.engine.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Test case for [TestCaseSourceRefTest], defined in a separate file so that the line numbers are stable.
 */
internal class MySpecForTestCaseSourceRefTest : FunSpec() {
   init {
      test("my test case") {
         1 shouldBe 1
      }
      test("test case 2") {
         1 shouldBe 1
      }
   }
}
