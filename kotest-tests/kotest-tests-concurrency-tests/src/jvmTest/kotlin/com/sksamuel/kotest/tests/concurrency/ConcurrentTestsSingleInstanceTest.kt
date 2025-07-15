package com.sksamuel.kotest.tests.concurrency

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.test.TestResult
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

// asserts that tests can be launched concurrently and before/after callbacks are handled properly
@EnabledIf(LinuxOnlyGithubCondition::class)
class ConcurrentTestsSingleInstanceTest : FunSpec() {

   private var befores = ""
   private var afters = ""
   private lateinit var start: TimeMark

   override fun isolationMode() = IsolationMode.SingleInstance
   override fun testExecutionMode(): TestExecutionMode? = TestExecutionMode.Concurrent
   override fun testCaseOrder() = TestCaseOrder.Sequential

   override suspend fun beforeTest(testCase: TestCase) {
      befores += testCase.name.name
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afters += testCase.name.name
   }

   override suspend fun beforeSpec(spec: Spec) {
      start = TimeSource.Monotonic.markNow() // We cannot use virtual time with concurrency
   }

   override suspend fun afterSpec(spec: Spec) {
      // The sum all delays is 1500 ms, but tests should run concurrently.
      start.elapsedNow() shouldBeLessThan 1499.milliseconds
      befores.shouldHaveLength(4)
      // beforeTest should be called in declaration order
      befores shouldBe "abcd"
      // all tests should be launched together, and so the delay will decide which finishes first
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
