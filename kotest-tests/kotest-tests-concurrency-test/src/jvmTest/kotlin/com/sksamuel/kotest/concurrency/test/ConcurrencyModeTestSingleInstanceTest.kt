package com.sksamuel.kotest.concurrency.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import kotlinx.coroutines.delay

// asserts that tests are launched concurrently, and that before/after test callbacks are called correctly
class ConcurrencyModeTestSingleInstanceTest : FunSpec() {

   private var befores = ""
   private var afters = ""
   private var start = 0L

   override fun isolationMode() = IsolationMode.SingleInstance

   override fun beforeTest(testCase: TestCase) {
      befores += testCase.displayName
   }

   override fun afterTest(testCase: TestCase, result: TestResult) {
      afters += testCase.displayName
   }

   override fun beforeSpec(spec: Spec) {
      start = System.currentTimeMillis()
   }

   override fun afterSpec(spec: Spec) {
      val end = System.currentTimeMillis()
      (end - start).shouldBeLessThan(500)
      befores.shouldHaveLength(4)
      befores shouldBe "abcd"
      afters shouldBe "cbad"
   }

   init {
      test("a") {
         delay(200)
      }
      test("b") {
         delay(100)
      }
      test("c") {
      }
      test("d") {
         delay(300)
      }
   }
}
