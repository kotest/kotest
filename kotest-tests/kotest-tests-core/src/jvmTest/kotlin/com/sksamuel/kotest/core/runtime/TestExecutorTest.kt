package com.sksamuel.kotest.core.runtime

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.runtime.CallingThreadExecutionContext
import io.kotest.core.runtime.TimeoutExecutionContext
import io.kotest.core.runtime.ExecutorExecutionContext
import io.kotest.core.runtime.TestExecutionListener
import io.kotest.core.runtime.TestExecutor
import io.kotest.core.runtime.TimeoutException
import io.kotest.core.spec.materializeRootTests
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
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

   fun context(testCase: TestCase) = object : TestContext() {
      override val testCase: TestCase = testCase
      override suspend fun registerTestCase(nested: NestedTest) {}
      override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
   }

   test("test executor happy path") {
      var started = false
      var finished = false
      val listener = object : TestExecutionListener {
         override fun testStarted(testCase: TestCase) {
            started = true
         }

         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.status shouldBe TestStatus.Success
         }
      }
      val executor = TestExecutor(listener, context)
      val testCase = Tests().materializeRootTests().first { it.testCase.name == "a" }.testCase
      executor.execute(testCase, context(testCase)).status shouldBe TestStatus.Success
      started shouldBe true
      finished shouldBe true
   }

   test("test executor should timeout a suspendable call") {
      var started = false
      var finished = false
      val listener = object : TestExecutionListener {
         override fun testStarted(testCase: TestCase) {
            started = true
         }

         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.status shouldBe TestStatus.Error
         }
      }
      val executor = TestExecutor(listener, context)
      val testCase = Tests().materializeRootTests().first { it.testCase.name == "b" }.testCase
      val result = executor.execute(testCase, context(testCase))
      result.status shouldBe TestStatus.Error
      result.error shouldBe TimeoutException(100.milliseconds)
      started shouldBe true
      finished shouldBe true
   }

   test("test executor should throw if validation throws") {
      val executor = TestExecutor(object : TestExecutionListener {
         override fun testStarted(testCase: TestCase) {}
         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {}
      }, context) { error("foo") }

      val testCase = Tests().materializeRootTests().first { it.testCase.name == "a" }.testCase

      shouldThrow<IllegalStateException> {
         executor.execute(testCase, context(testCase))
      }
   }

   test("test executor should invoke before test") {
      val executor = TestExecutor(object : TestExecutionListener {
         override fun testStarted(testCase: TestCase) {}
         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {}
      }, context)
      val spec = BeforeTest()
      val testCase = spec.materializeRootTests().first().testCase
      executor.execute(testCase, context(testCase))
      spec.before.shouldBeTrue()
   }

   test("test executor should invoke after test") {
      val executor = TestExecutor(object : TestExecutionListener {
         override fun testStarted(testCase: TestCase) {}
         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {}
      }, context)
      val spec = AfterTest()
      val testCase = spec.materializeRootTests().first().testCase
      executor.execute(testCase, context(testCase))
      spec.after.shouldBeTrue()
   }

   test("test executor should start/finish test with error if before-test throws") {
      var started = false
      var finished = false
      val executor = TestExecutor(object : TestExecutionListener {
         override fun testStarted(testCase: TestCase) {
            started = true
         }

         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, context)
      val testCase = BeforeTestWithException().materializeRootTests().first().testCase
      val result = executor.execute(testCase, context(testCase))
      result.status shouldBe TestStatus.Error
      result.error.shouldBeInstanceOf<IllegalStateException>()
      started shouldBe true
      finished shouldBe true
   }

   test("test executor should start/finish test with error if after-test throws") {
      var started = false
      var finished = false
      val executor = TestExecutor(object : TestExecutionListener {
         override fun testStarted(testCase: TestCase) {
            started = true
         }

         override fun testIgnored(testCase: TestCase) {}
         override fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, context)
      val testCase = AfterTestWithException().materializeRootTests().first().testCase
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
internal class Tests : FunSpec({
   test("a") {}
   test("b").config(timeout = 100.milliseconds) { delay(1000000) }
})


internal class BeforeTest : FunSpec() {
   var before = false

   init {
      beforeTest {
         before = true
      }
      test("a") {}
   }
}

internal class BeforeTestWithException : FunSpec({
   beforeTest {
      error("boom")
   }
   test("a") {}
})

internal class AfterTest : FunSpec() {
   var after = false

   init {
      afterTest {
         after = true
      }
      test("a") {}
   }
}

internal class AfterTestWithException : FunSpec({
   afterTest {
      error("boom")
   }
   test("a") {}
})
