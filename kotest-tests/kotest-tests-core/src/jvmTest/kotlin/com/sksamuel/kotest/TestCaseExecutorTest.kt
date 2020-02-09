//package com.sksamuel.kotest
//
//import com.nhaarman.mockito_kotlin.argThat
//import com.nhaarman.mockito_kotlin.mock
//import com.nhaarman.mockito_kotlin.then
//import io.kotest.core.test.Description
//import io.kotest.core.test.TestCase
//import io.kotest.assertions.currentTimeMillis
//import io.kotest.core.test.TestCaseConfig
//import io.kotest.core.test.TestContext
//import io.kotest.runner.jvm.TestExecutor
//import io.kotest.runner.jvm.TestEngineListener
//import io.kotest.shouldBe
//import io.kotest.shouldNotBe
//import io.kotest.specs.FunSpec
//import kotlinx.coroutines.GlobalScope
//import java.util.concurrent.Executors
//import java.util.concurrent.atomic.AtomicInteger
//import kotlin.coroutines.CoroutineContext
//import kotlin.time.ExperimentalTime
//import kotlin.time.milliseconds
//
//@ExperimentalTime
//@Suppress("BlockingMethodInNonBlockingContext")
//class TestCaseExecutorTest : FunSpec() {
//
//   private val scheduler = Executors.newScheduledThreadPool(1)
//
//   init {
//

//
//      test("tests which timeout should error").config {
//         val listenerExecutor = Executors.newSingleThreadExecutor()
//         val listener = mock<TestEngineListener> {}
//         val executor = TestExecutor(listener, listenerExecutor, scheduler)
//
//         val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
//            Thread.sleep(10000)
//         }.copy(config = TestCaseConfig(true, invocations = 1, threads = 1, timeout = 100.milliseconds))
//
//         val context = object : TestContext(GlobalScope.coroutineContext) {
//            override suspend fun registerTestCase(testCase: TestCase) {}
//            override fun description(): Description = Description.spec("wibble")
//         }
//         executor.execute(testCase, context)
//
//         then(listener).should().exitTestCase(
//            argThat { description == Description.spec("wibble") },
//            argThat { status == TestStatus.Error && this.error?.message == "Execution of test took longer than 100ms" }
//         )
//      }
//
//      test("test with loop that takes longer than timeout should complete with an error") {
//
//         val listenerExecutor = Executors.newSingleThreadExecutor()
//         val listener = mock<TestEngineListener> {}
//         val executor = TestExecutor(listener, listenerExecutor, scheduler)
//
//         val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
//            val startTime = currentTimeMillis()
//            while (currentTimeMillis() < startTime + 1000) {
//               "this" shouldNotBe "that"
//            }
//         }.copy(config = TestCaseConfig(true, invocations = 1, threads = 1, timeout = 125.milliseconds))
//
//         val context = object : TestContext(GlobalScope.coroutineContext) {
//            override suspend fun registerTestCase(testCase: TestCase) {}
//            override fun description(): Description = Description.spec("wibble")
//         }
//
//         executor.execute(testCase, context)
//
//         then(listener).should().exitTestCase(
//            argThat { description == Description.spec("wibble") },
//            argThat { status == TestStatus.Error && this.error?.message == "Execution of test took longer than 125ms" }
//         )
//      }
//
//      test("test with infinite loop but invocations = 1 should complete with TestStatus.Failure") {
//
//         val listenerExecutor = Executors.newSingleThreadExecutor()
//         val listener = mock<TestEngineListener> {}
//         val executor = TestExecutor(listener, listenerExecutor, scheduler)
//
//         val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
//            while (true) {
//               "this" shouldBe "that"
//            }
//         }.copy(config = TestCaseConfig(true, invocations = 1, threads = 1))
//
//         val context = object : TestContext(GlobalScope.coroutineContext) {
//            override suspend fun registerTestCase(testCase: TestCase) {}
//            override fun description(): Description = Description.spec("wibble")
//         }
//         executor.execute(testCase, context)
//
//         then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") },
//            argThat { status == TestStatus.Failure })
//      }
//
//      test("test with infinite loop but invocations > 1 should complete with TestStatus.Failure") {
//
//         val listenerExecutor = Executors.newSingleThreadExecutor()
//         val listener = mock<TestEngineListener> {}
//         val executor = TestExecutor(listener, listenerExecutor, scheduler)
//
//         val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseExecutorTest) {
//            while (true) {
//               "this" shouldBe "that"
//            }
//         }.copy(config = TestCaseConfig(true, invocations = 2, threads = 1))
//
//         val context = object : TestContext(GlobalScope.coroutineContext) {
//            override suspend fun registerTestCase(testCase: TestCase) {}
//            override fun description(): Description = Description.spec("wibble")
//         }
//         executor.execute(testCase, context)
//
//         then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") },
//            argThat { status == TestStatus.Failure })
//      }
//   }
//}
//
