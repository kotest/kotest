package com.sksamuel.kotest.engine.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
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
class TestCaseExecutorTest : FunSpec({

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
            result.isSuccess shouldBe true
         }
      }
      val executor = TestCaseExecutor(listener, NoopCoroutineDispatcherFactory)
      val testCase = Tests().materializeAndOrderRootTests().first { it.testCase.name.testName == "a" }.testCase
      executor.execute(testCase, context(testCase)).isSuccess shouldBe true
      started shouldBe true
      finished shouldBe true
   }

   test("TestCaseExecutor should timeout a suspendable call") {
      var started = false
      var finished = false
      val listener = object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {
            started = true
         }

         override suspend fun testIgnored(testCase: TestCase) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.isError shouldBe true
         }
      }
      val executor = TestCaseExecutor(listener, NoopCoroutineDispatcherFactory)
      val testCase = Tests().materializeAndOrderRootTests().first { it.testCase.name.testName == "b" }.testCase
      val result = executor.execute(testCase, context(testCase))
      result.isError shouldBe true
      result.errorOrNull shouldBe TestTimeoutException(100, "b")
      started shouldBe true
      finished shouldBe true
   }

   test("TestCaseExecutor should invoke before test") {
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {}
         override suspend fun testIgnored(testCase: TestCase) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {}
      }, NoopCoroutineDispatcherFactory)
      val spec = BeforeTest()
      val testCase = spec.materializeAndOrderRootTests().first().testCase
      executor.execute(testCase, context(testCase))
      spec.before.shouldBeTrue()
   }

   test("TestCaseExecutor should invoke after test") {
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {}
         override suspend fun testIgnored(testCase: TestCase) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {}
      }, NoopCoroutineDispatcherFactory)
      val spec = AfterTest()
      val testCase = spec.materializeAndOrderRootTests().first().testCase
      executor.execute(testCase, context(testCase))
      spec.after.shouldBeTrue()
   }

   test("TestCaseExecutor should start/finish test with error if before-test throws") {
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
      }, NoopCoroutineDispatcherFactory)
      val testCase = BeforeTestWithException().materializeAndOrderRootTests().first().testCase
      val result = executor.execute(testCase, context(testCase))
      result.isError shouldBe true
      result.errorOrNull.shouldBeInstanceOf<IllegalStateException>()
      started shouldBe true
      finished shouldBe true
   }

   test("TestCaseExecutor should start/finish test with error if after-test throws") {
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
      }, NoopCoroutineDispatcherFactory)
      val testCase = AfterTestWithException().materializeAndOrderRootTests().first().testCase
      val result = executor.execute(testCase, context(testCase))
      result.isError shouldBe true
      result.errorOrNull.shouldBeInstanceOf<IllegalStateException>()
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
