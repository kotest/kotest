package com.sksamuel.kotlintest

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import io.kotlintest.*
import io.kotlintest.runner.jvm.TestCaseExecutor
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.FunSpec
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.GlobalScope
import java.lang.System.currentTimeMillis
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

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
          argThat { status == TestStatus.Error && this.error?.message == "Execution of test took longer than PT0.1S" }
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
          argThat { status == TestStatus.Error && this.error?.message == "Execution of test took longer than PT0.125S" }
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

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest, {
        while (true) {
          "this" shouldBe "that"
        }
      }).copy(config = TestCaseConfig(true, invocations = 2, threads = 1))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Failure })
    }

    test("test with failure at beforeTest should complete with TestStatus.Failure after starting the test") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mockk<TestEngineListener>(relaxed = true)
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), object : FreeSpec() {
        override fun beforeTest(testCase: TestCase) {
          throw RuntimeException("Failure!")
        }
      }) {
        "this" shouldBe "this"
      }

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      verify { listener.invokingTestCase(match { it.description == Description.spec("wibble") }, 1) }
      verify { listener.exitTestCase(match { it.description == Description.spec("wibble") }, match { it.status == TestStatus.Error }) }
    }

    test("test with failure at afterTest should complete with TestStatus.Failure") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), object : FreeSpec() {
        override fun afterTest(testCase: TestCase, result: TestResult) {
          throw RuntimeException("Failure!")
        }
      }) {
        "this" shouldBe "this"
      }

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Error })
    }
  }
}
