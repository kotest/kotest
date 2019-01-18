package com.sksamuel.kotlintest

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import io.kotlintest.Description
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestStatus
import io.kotlintest.TestType
import io.kotlintest.milliseconds
import io.kotlintest.runner.jvm.TestCaseExecutor
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.FunSpec
import kotlinx.coroutines.GlobalScope
import java.lang.System.currentTimeMillis
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class TestCaseExecutorTest : FunSpec() {

  private val scheduler = Executors.newScheduledThreadPool(1)

  init {

    test("single thread / single invocation") {

      var counter = 0

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val executor = TestCaseExecutor(object : TestEngineListener {}, listenerExecutor, scheduler)
      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        counter++
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 1, threads = 1))
      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)
      counter.shouldBe(1)
    }

    test("single thread / many invocations") {

      var counter = 0

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val executor = TestCaseExecutor(object : TestEngineListener {}, listenerExecutor, scheduler)
      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        counter++
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 10, threads = 1))
      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      counter.shouldBe(10)
    }

    test("many threads / many invocations should complete") {

      val counter = AtomicInteger(0)
      val threadLocal = ThreadLocal.withInitial { false }

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val executor = TestCaseExecutor(object : TestEngineListener {}, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        counter.incrementAndGet()
        threadLocal.set(true)
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 50, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
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

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        counter.incrementAndGet()
        threadLocal.set(true)
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 1, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      counter.get() shouldBe 1
      threadLocal.get() shouldBe false
    }

    test("many threads / single invocation with a failure should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        "a" shouldBe "b"
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 1, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.root("wibble") }, argThat { status == TestStatus.Failure })
    }

    test("many threads / single invocation with an error should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        throw RuntimeException()
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 1, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.root("wibble") }, argThat { status == TestStatus.Error })
    }

    test("many threads / many threads with a failure should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        "a" shouldBe "b"
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 10, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.root("wibble") }, argThat { status == TestStatus.Failure })
    }

    test("many threads / many threads with an error should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        throw RuntimeException()
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 10, threads = 5))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.root("wibble") }, argThat { status == TestStatus.Error })
    }

    test("single thread / many threads with a failure should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        "a" shouldBe "b"
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 10, threads = 1))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.root("wibble") }, argThat { status == TestStatus.Failure })
    }

    test("single thread / many threads with an error should complete") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        throw RuntimeException()
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 10, threads = 1))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.root("wibble") }, argThat { status == TestStatus.Error })
    }

    test("tests which block should timeout should error").config {
      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        Thread.sleep(100000000L)
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 1, threads = 1, timeout = 100.milliseconds))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(
          argThat { description == Description.root("wibble") },
          argThat { status == TestStatus.Error && this.error?.message == "Execution of test took longer than PT0.1S" }
      )
    }

    test("test with loop that takes longer than timeout should complete with an error") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        val startTime = currentTimeMillis()
        while (currentTimeMillis() < startTime + 1000) {
          "this" shouldNotBe "that"
        }
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 1, threads = 1, timeout = 125.milliseconds))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(
          argThat { description == Description.root("wibble") },
          argThat { status == TestStatus.Error && this.error?.message == "Execution of test took longer than PT0.125S" }
      )
    }

    test("test with infinite loop but failure should complete with TestStatus.Failure") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase(Description.root("wibble"), this@TestCaseExecutorTest, {
        while (true) {
          "this" shouldBe "that"
        }
      }, 0, TestType.Test, TestCaseConfig(true, invocations = 1, threads = 1))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.root("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.root("wibble") }, argThat { status == TestStatus.Failure })
    }
  }
}

class MultipleTestTimeoutTest : FreeSpec() {

  // The test executor was failing because as it reutilizes some threads from a thread pool.
  // When using that thread pool, a task to cancel the thread is created, so that the engine can interrupt
  // a test that is going forever.
  // However, if the task is not cancelled, it will eventually interrupt the thread when it's running another task
  // in the thread pool, interrupting a test that hasn't timed out yet, which is undesired.

  init {
    // 100 millis sleep will "accumulate" between tests. If the context is still shared, one of them will fail
    // due to timeout.
    "Test 1".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 2".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 3".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 4".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 5".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 6".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 7".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }
  }
}