package com.sksamuel.kotest.concurrency.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import kotlinx.coroutines.delay

// asserts that tests are launched concurrently, and that before/after test callbacks are called
class ConcurrencyModeTestSingleInstanceTest : FunSpec() {

   private var str = ""
   private var start = 0L

   override fun isolationMode() = IsolationMode.SingleInstance

   override fun afterTest(testCase: TestCase, result: TestResult) {
      str += testCase.displayName
   }

   override fun beforeSpec(spec: Spec) {
      start = System.currentTimeMillis()
   }

   override fun afterSpec(spec: Spec) {
      val end = System.currentTimeMillis()
      (end - start).shouldBeLessThan(500)
      str.shouldHaveLength(4)
      str shouldNotBe "dcba"
   }

   init {
      test("a") {
         delay(250)
      }
      test("b") {
         delay(50)
      }
      test("c") {
      }
      test("d") {
         delay(250)
      }
   }
}
