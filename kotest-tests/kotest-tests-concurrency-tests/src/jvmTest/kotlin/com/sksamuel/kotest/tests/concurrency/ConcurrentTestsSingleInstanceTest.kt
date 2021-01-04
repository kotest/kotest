package com.sksamuel.kotest.tests.concurrency

import io.kotest.core.config.Configuration
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import kotlinx.coroutines.delay

// asserts that tests can be launched concurrently and before/after callbacks are handled properly
class ConcurrentTestsSingleInstanceTest : FunSpec() {

   private var befores = ""
   private var afters = ""
   private var start = 0L

   override fun isolationMode() = IsolationMode.SingleInstance
   override fun concurrency(): Int = Configuration.MaxConcurrency
   override fun testCaseOrder() = TestCaseOrder.Sequential

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
      // total of delays is 1500 but should run concurrently
      (end - start).shouldBeLessThan(1000)
      befores.shouldHaveLength(4)
      // beforeTest should be called in declaration order
      befores shouldBe "abcd"
      // all tests should be launched together and so the delay will decide which finishes first
      afters shouldBe "cbad"
   }

   init {
      test("a") {
         delay(500)
      }
      test("b") {
         delay(250)
      }
      test("c") {
      }
      test("d") {
         delay(750)
      }
   }
}
