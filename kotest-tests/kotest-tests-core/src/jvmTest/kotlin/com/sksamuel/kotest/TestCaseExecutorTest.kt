package com.sksamuel.kotest

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import io.kotest.Description
import io.kotest.TestCase
import io.kotest.TestStatus
import io.kotest.assertions.currentTimeMillis
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.runner.jvm.TestCaseExecutor
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.shouldBe
import io.kotest.shouldNotBe
import io.kotest.specs.FunSpec
import kotlinx.coroutines.GlobalScope
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
@Suppress("BlockingMethodInNonBlockingContext")
class TestCaseExecutorTest : FunSpec() {

  private val scheduler = Executors.newScheduledThreadPool(1)

  init {

    test("single thread / single invocation") {

      var counter = 0

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val executor = TestCaseExecutor(object : TestEngineListener {}, listenerExecutor, scheduler)
      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) { counter++ }
      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)
      counter.shouldBe(1)
    }

    test("single thread / many invocations") {

      var counter = 0

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val executor = TestCaseExecutor(object : TestEngineListener {}, listenerExecutor, scheduler)
      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        counter++
      }.copy(config = TestCaseConfig(true, invocations = 10, threads = 1))
      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      counter.shouldBe(10)
    }

    test("many threads / many invocations should complete") {

      val counter = AtomicInteger(0)
      val threadLocal = ThreadLocal.withInitial { false }

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val executor = TestCaseExecutor(object : TestEngineListener {}, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        counter.incrementAndGet()
        threadLocal.set(true)
      }.copy(config = TestCaseConfig(true, invocations = 50, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      counter.get() shouldBe 50
      threadLocal.get() shouldBe false
    }

    test("many threads / single invocations should complete") {

      val counter = AtomicInteger(0)
      val threadLocal = ThreadLocal.withInitial { false }

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val executor = TestCaseExecutor(object : TestEngineListener {}, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        counter.incrementAndGet()
        threadLocal.set(true)
      }.copy(config = TestCaseConfig(true, invocations = 1, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      counter.get() shouldBe 1
      threadLocal.get() shouldBe false
    }

    test("many threads / single invocation with a failure should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        "a" shouldBe "b"
      }.copy(config = TestCaseConfig(true, invocations = 1, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Failure })
    }

    test("many threads / single invocation with an error should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        throw RuntimeException()
      }.copy(config = TestCaseConfig(true, invocations = 1, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Error })
    }

    test("many threads / many threads with a failure should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        "a" shouldBe "b"
      }.copy(config = TestCaseConfig(true, invocations = 10, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Failure })
    }

    test("many threads / many threads with an error should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        throw RuntimeException()
      }.copy(config = TestCaseConfig(true, invocations = 10, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Error })
    }

    test("single thread / many threads with a failure should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        "a" shouldBe "b"
      }.copy(config = TestCaseConfig(true, invocations = 10, threads = 1))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Failure })
    }

    test("single thread / many threads with an error should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        throw RuntimeException()
      }.copy(config = TestCaseConfig(true, invocations = 10, threads = 1))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Error })
    }

    test("tests which timeout should error").config {
      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        Thread.sleep(10000)
      }.copy(config = TestCaseConfig(true, invocations = 1, threads = 1, timeout = 100.milliseconds))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(
          argThat { description == Description.spec("wibble") },
          argThat { status == TestStatus.Error && this.error?.message == "Execution of test took longer than 100ms" }
      )
    }

    test("test with loop that takes longer than timeout should complete with an error") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        val startTime = currentTimeMillis()
        while (currentTimeMillis() < startTime + 1000) {
          "this" shouldNotBe "that"
        }
      }.copy(config = TestCaseConfig(true, invocations = 1, threads = 1, timeout = 125.milliseconds))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }

      executor.execute(testCase, context)

      then(listener).should().exitTestCase(
          argThat { description == Description.spec("wibble") },
          argThat { status == TestStatus.Error && this.error?.message == "Execution of test took longer than 125ms" }
      )
    }

    test("test with infinite loop but invocations = 1 should complete with TestStatus.Failure") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        while (true) {
          "this" shouldBe "that"
        }
      }.copy(config = TestCaseConfig(true, invocations = 1, threads = 1))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Failure })
    }

    test("test with infinite loop but invocations > 1 should complete with TestStatus.Failure") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
        while (true) {
          "this" shouldBe "that"
        }
      }.copy(config = TestCaseConfig(true, invocations = 2, threads = 1))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Failure })
    }
  }
}

