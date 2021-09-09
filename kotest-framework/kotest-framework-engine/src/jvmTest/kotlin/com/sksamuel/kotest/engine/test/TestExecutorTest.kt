package com.sksamuel.kotest.engine.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.NoopCoroutineDispatcherController
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.interceptors.TestTimeoutException
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

@DelicateCoroutinesApi
class TestExecutorTest : FunSpec({

   fun context(testCase: TestCase) = object : TestContext {
      override val testCase: TestCase = testCase
      override suspend fun registerTestCase(nested: NestedTest) {}
      override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
   }

   test("test executor happy path") {
      var started = false
      var finished = false
      val listener = object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {
            started = true
         }

         override suspend fun testIgnored(testCase: TestCase) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.status shouldBe TestStatus.Success
         }
      }
      val executor = TestCaseExecutor(listener, NoopCoroutineDispatcherController)
      val testCase = Tests().materializeAndOrderRootTests().first { it.testCase.displayName == "a" }.testCase
      executor.execute(testCase, context(testCase)).status shouldBe TestStatus.Success
      started shouldBe true
      finished shouldBe true
   }

   test("test executor should timeout a suspendable call") {
      var started = false
      var finished = false
      val listener = object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {
            started = true
         }

         override suspend fun testIgnored(testCase: TestCase) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.status shouldBe TestStatus.Error
         }
      }
      val executor = TestCaseExecutor(listener, NoopCoroutineDispatcherController)
      val testCase = Tests().materializeAndOrderRootTests().first { it.testCase.displayName == "b" }.testCase
      val result = executor.execute(testCase, context(testCase))
      result.status shouldBe TestStatus.Error
      result.error shouldBe TestTimeoutException(100, "b")
      started shouldBe true
      finished shouldBe true
   }

   test("test executor should invoke before test") {
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {}
         override suspend fun testIgnored(testCase: TestCase) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {}
      }, NoopCoroutineDispatcherController)
      val spec = BeforeTest()
      val testCase = spec.materializeAndOrderRootTests().first().testCase
      executor.execute(testCase, context(testCase))
      spec.before.shouldBeTrue()
   }

   test("test executor should invoke after test") {
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {}
         override suspend fun testIgnored(testCase: TestCase) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {}
      }, NoopCoroutineDispatcherController)
      val spec = AfterTest()
      val testCase = spec.materializeAndOrderRootTests().first().testCase
      executor.execute(testCase, context(testCase))
      spec.after.shouldBeTrue()
   }

   test("test executor should start/finish test with error if before-test throws") {
      var started = false
      var finished = false
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {
            started = true
         }

         override suspend fun testIgnored(testCase: TestCase) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, NoopCoroutineDispatcherController)
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
         override suspend fun testStarted(testCase: TestCase) {
            started = true
         }

         override suspend fun testIgnored(testCase: TestCase) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, NoopCoroutineDispatcherController)
      val testCase = AfterTestWithException().materializeAndOrderRootTests().first().testCase
      val result = executor.execute(testCase, context(testCase))
      result.status shouldBe TestStatus.Error
      result.error.shouldBeInstanceOf<IllegalStateException>()
      started shouldBe true
      finished shouldBe true
   }
})

private class Tests : FunSpec({
   test("a") {}
   test("b").config(timeout = Duration.milliseconds(100)) { delay(1000000) }
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
