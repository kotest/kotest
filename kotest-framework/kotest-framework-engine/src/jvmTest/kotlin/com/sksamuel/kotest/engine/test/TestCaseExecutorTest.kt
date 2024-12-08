package com.sksamuel.kotest.engine.test

import io.kotest.core.Platform
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.interceptor.SpecContext
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
import kotlin.time.Duration.Companion.milliseconds

@OptIn(DelicateCoroutinesApi::class)
@EnabledIf(LinuxCondition::class)
class TestCaseExecutorTest : FunSpec({

   fun context(testCase: TestCase) = object : TestScope {
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

         override suspend fun testIgnored(testCase: TestCase, reason: String?) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.isSuccess shouldBe true
         }
      }
      val executor = TestCaseExecutor(listener, NoopCoroutineDispatcherFactory, EngineContext(ProjectConfiguration(), Platform.JVM))
      val testCase = Materializer(ProjectConfiguration()).materialize(Tests()).first { it.name.testName == "a" }
      executor.execute(testCase, context(testCase), SpecContext.create()).isSuccess shouldBe true
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

         override suspend fun testIgnored(testCase: TestCase, reason: String?) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
            result.isError shouldBe true
         }
      }
      val executor = TestCaseExecutor(listener, NoopCoroutineDispatcherFactory, EngineContext(ProjectConfiguration(), Platform.JVM))
      val testCase = Materializer(ProjectConfiguration()).materialize(Tests()).first { it.name.testName == "b" }
      val result = executor.execute(testCase, context(testCase), SpecContext.create())
      result.isError shouldBe true
      result.errorOrNull shouldBe TestTimeoutException(100.milliseconds, "b")
      started shouldBe true
      finished shouldBe true
   }

   test("TestCaseExecutor should invoke before test") {
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {}
         override suspend fun testIgnored(testCase: TestCase, reason: String?) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {}
      }, NoopCoroutineDispatcherFactory, EngineContext(ProjectConfiguration(), Platform.JVM))
      val spec = BeforeTest()
      val testCase = Materializer(ProjectConfiguration()).materialize(spec).shuffled().first()
      executor.execute(testCase, context(testCase), SpecContext.create())
      spec.before.shouldBeTrue()
   }

   test("TestCaseExecutor should invoke after test") {
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {}
         override suspend fun testIgnored(testCase: TestCase, reason: String?) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {}
      }, NoopCoroutineDispatcherFactory, EngineContext(ProjectConfiguration(), Platform.JVM))
      val spec = AfterTest()
      val testCase = Materializer(ProjectConfiguration()).materialize(spec).shuffled().first()
      executor.execute(testCase, context(testCase), SpecContext.create())
      spec.after.shouldBeTrue()
   }

   test("TestCaseExecutor should start/finish test with error if before-test throws") {
      var started = false
      var finished = false
      val executor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {
            started = true
         }

         override suspend fun testIgnored(testCase: TestCase, reason: String?) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, NoopCoroutineDispatcherFactory, EngineContext(ProjectConfiguration(), Platform.JVM))
      val testCase = Materializer(ProjectConfiguration()).materialize(BeforeTestWithException()).shuffled().first()
      val result = executor.execute(testCase, context(testCase), SpecContext.create())
      result.isError shouldBe true
      result.errorOrNull.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
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

         override suspend fun testIgnored(testCase: TestCase, reason: String?) {}
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            finished = true
         }
      }, NoopCoroutineDispatcherFactory, EngineContext(ProjectConfiguration(), Platform.JVM))
      val testCase = Materializer(ProjectConfiguration()).materialize(AfterTestWithException()).shuffled().first()
      val result = executor.execute(testCase, context(testCase), SpecContext.create())
      result.isError shouldBe true
      result.errorOrNull.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
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
