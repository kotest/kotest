package com.sksamuel.kotest.specs

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

// tests that afterTest is called before the next beforeTest for single spec runner
class BeforeAfterTestOrder : FunSpec() {

   private var seq = ""

   override fun afterSpec(spec: Spec) {
      seq shouldBe "cadbcadbcadb"
   }

   override fun beforeTest(testCase: TestCase) {
      seq += "a"
   }

   override fun afterTest(testCase: TestCase, result: TestResult) {
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
