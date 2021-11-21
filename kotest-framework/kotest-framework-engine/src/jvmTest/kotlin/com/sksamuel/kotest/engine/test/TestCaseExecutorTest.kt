package com.sksamuel.kotest.engine.test

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.Configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.NoopTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.interceptors.TestTimeoutException
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalKotest
@DelicateCoroutinesApi
class TestCaseExecutorTest : FunSpec({

   test("test executor happy path") {
      var started = false
      var finished = false
      val listener = object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {
            started = true
         }

         override suspend fun testIgnored(testCase: TestCase, result: TestResult) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.isSuccess shouldBe true
         }
      }
      val executor = TestCaseExecutor(listener, NoopCoroutineDispatcherFactory, Configuration())
      val testCase = Materializer(Configuration()).materialize(Tests()).first { it.name.testName == "a" }
      executor.execute(testCase).isSuccess shouldBe true
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

         override suspend fun testIgnored(testCase: TestCase, result: TestResult) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.isError shouldBe true
         }
      }
      val executor = TestCaseExecutor(listener, NoopCoroutineDispatcherFactory, Configuration())
      val testCase = Materializer(Configuration()).materialize(Tests()).first { it.name.testName == "b" }
      val result = executor.execute(testCase)
      result.isError shouldBe true
      result.errorOrNull shouldBe TestTimeoutException(100.milliseconds, "b")
      started shouldBe true
      finished shouldBe true
   }

   test("TestCaseExecutor should invoke before test") {
      val executor = TestCaseExecutor(NoopTestCaseExecutionListener, NoopCoroutineDispatcherFactory, Configuration())
      val spec = BeforeTest()
      val testCase = Materializer(Configuration()).materialize(spec).shuffled().first()
      executor.execute(testCase)
      spec.before.shouldBeTrue()
   }

   test("TestCaseExecutor should invoke after test") {
      val executor = TestCaseExecutor(NoopTestCaseExecutionListener, NoopCoroutineDispatcherFactory, Configuration())
      val spec = AfterTest()
      val testCase = Materializer(Configuration()).materialize(spec).shuffled().first()
      executor.execute(testCase)
      spec.after.shouldBeTrue()
   }

   test("TestCaseExecutor should start/finish test with error if before-test throws") {
      var started = false
      var finished = false
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {
            started = true
         }

         override suspend fun testIgnored(testCase: TestCase, result: TestResult) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, NoopCoroutineDispatcherFactory, Configuration())
      val testCase = Materializer(Configuration()).materialize(BeforeTestWithException()).shuffled().first()
      val result = executor.execute(testCase)
      result.isError shouldBe true
      result.errorOrNull.shouldBeInstanceOf<ExtensionException.BeforeTestException>()
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

         override suspend fun testIgnored(testCase: TestCase, result: TestResult) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, NoopCoroutineDispatcherFactory, Configuration())
      val testCase = Materializer(Configuration()).materialize(AfterTestWithException()).shuffled().first()
      val result = executor.execute(testCase)
      result.isError shouldBe true
      result.errorOrNull.shouldBeInstanceOf<ExtensionException.AfterTestException>()
      started shouldBe true
      finished shouldBe true
   }
})

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
