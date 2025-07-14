package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe

// tests that afterTest is called before the next beforeTest for single spec runner
class FunSpecCallbackOrderTest : FunSpec() {

   private var seq = ""

   override suspend fun afterSpec(spec: Spec) {
      seq shouldBe "acbdacbdacbd"
   }

   override suspend fun beforeTest(testCase: TestCase) {
      seq += "a"
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      seq += "b"
   }

   init {

      beforeTest {
         seq += "c"
      }

      afterTest {
         seq += "d"
      }

      test("1") {}

      test("2") {}

      test("3") {}
   }
}
