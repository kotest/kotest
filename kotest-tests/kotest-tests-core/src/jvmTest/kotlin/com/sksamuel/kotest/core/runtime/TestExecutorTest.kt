package com.sksamuel.kotest.core.runtime

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.CallingThreadExecutionContext
import io.kotest.core.TimeoutExecutionContext
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.core.test.TestCaseExecutionListener
import io.kotest.core.internal.TestCaseExecutor
import io.kotest.core.internal.TimeoutException
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.engine.toTestResult
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
fun testExecutorTests(context: TimeoutExecutionContext) = funSpec {

   fun context(testCase: TestCase) = object : TestContext {
      override val testCase: TestCase = testCase
      override suspend fun registerTestCase(nested: NestedTest) {}
      override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
   }

   test("test executor happy path") {
      var started = false
      var finished = false
      val listener = object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {
            started = true
         }

         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.status shouldBe TestStatus.Success
         }
      }
      val executor = TestCaseExecutor(listener, context, {}, ::toTestResult)
      val testCase = Tests().materializeAndOrderRootTests().first { it.testCase.displayName == "a" }.testCase
      executor.execute(testCase, context(testCase)).status shouldBe TestStatus.Success
      started shouldBe true
      finished shouldBe true
   }

   test("test executor should timeout a suspendable call") {
      var started = false
      var finished = false
      val listener = object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {
            started = true
         }

         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.status shouldBe TestStatus.Error
         }
      }
      val executor = TestCaseExecutor(listener, context, {}, ::toTestResult)
      val testCase = Tests().materializeAndOrderRootTests().first { it.testCase.displayName == "b" }.testCase
      val result = executor.execute(testCase, context(testCase))
      result.status shouldBe TestStatus.Error
      result.error shouldBe TimeoutException(100)
      started shouldBe true
      finished shouldBe true
   }

   test("test executor should throw if validation throws") {
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {}
         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {}
      }, context, { error("foo") }, ::toTestResult)

      val testCase = Tests().materializeAndOrderRootTests().first { it.testCase.displayName == "a" }.testCase

      shouldThrow<IllegalStateException> {
         executor.execute(testCase, context(testCase))
      }
   }

   test("test executor should invoke before test") {
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {}
         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {}
      }, context, {}, ::toTestResult)
      val spec = BeforeTest()
      val testCase = spec.materializeAndOrderRootTests().first().testCase
      executor.execute(testCase, context(testCase))
      spec.before.shouldBeTrue()
   }

   test("test executor should invoke after test") {
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {}
         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {}
      }, context, {}, ::toTestResult)
      val spec = AfterTest()
      val testCase = spec.materializeAndOrderRootTests().first().testCase
      executor.execute(testCase, context(testCase))
      spec.after.shouldBeTrue()
   }

   test("test executor should start/finish test with error if before-test throws") {
      var started = false
      var finished = false
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {
            started = true
         }

         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, context, {}, ::toTestResult)
      val testCase = BeforeTestWithException().materializeAndOrderRootTests().first().testCase
      val result = executor.execute(testCase, context(testCase))
      result.status shouldBe TestStatus.Error
      result.error.shouldBeInstanceOf<IllegalStateException>()
      started shouldBe true
      finished shouldBe true
   }

   test("test executor should start/finish test with error if after-test throws") {
      var started = false
      var finished = false
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {
            started = true
         }

         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, context, {}, ::toTestResult)
      val testCase = AfterTestWithException().materializeAndOrderRootTests().first().testCase
      val result = executor.execute(testCase, context(testCase))
      result.status shouldBe TestStatus.Error
      result.error.shouldBeInstanceOf<IllegalStateException>()
      started shouldBe true
      finished shouldBe true
   }
}

class TestExecutorTest : FunSpec({
   include("calling thread:", testExecutorTests(CallingThreadExecutionContext))
   include("executor:", testExecutorTests(ExecutorExecutionContext))
})

@OptIn(ExperimentalTime::class)
private class Tests : FunSpec({
   test("a") {}
   test("b").config(timeout = 100.milliseconds) { delay(1000000) }
})


private class BeforeTest : FunSpec() {
   var before = false

   init {
      beforeTest {
         before = true
      }
      test("a") {}
   }
}

private class BeforeTestWithException : FunSpec({
   beforeTest {
      error("boom")
   }
   test("a") {}
})

private class AfterTest : FunSpec() {
   var after = false

   init {
      afterTest {
         after = true
      }
      test("a") {}
   }
}

private class AfterTestWithException : FunSpec({
   afterTest {
      error("boom")
   }
   test("a") {}
})
