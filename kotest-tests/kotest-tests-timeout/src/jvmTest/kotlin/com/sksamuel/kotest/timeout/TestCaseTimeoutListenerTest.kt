//package com.sksamuel.kotest.timeout
//
//import io.kotest.core.runtime.TestExecutor
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.test.Description
//import io.kotest.core.test.NestedTest
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestCaseConfig
//import io.kotest.core.test.TestContext
//import io.kotest.matchers.shouldBe
//import io.kotest.core.engine.TestEngineListener
//import kotlinx.coroutines.GlobalScope
//import kotlin.coroutines.CoroutineContext
//import kotlin.time.ExperimentalTime
//import kotlin.time.milliseconds
//
//@Suppress("BlockingMethodInNonBlockingContext")
//@ExperimentalTime
//class TestCaseTimeoutListenerTest : FunSpec() {
//
//   private var listenerRan = false
//
//   init {
//
//      afterTest {
//         listenerRan = true
//      }
//
//      afterSpec {
//         listenerRan shouldBe true
//      }
//
//      test("tests which timeout during a blocking operation should run the 'after test' listeners").config(timeout = 1000.milliseconds) {
//
//         val listener = object : TestEngineListener {}
//         val executor = TestExecutor(listener)
//
//         val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseTimeoutListenerTest) {
//            Thread.sleep(5000)
//         }.copy(
//            config = TestCaseConfig(
//               true,
//               invocations = 1,
//               threads = 1,
//               timeout = 125.milliseconds
//            )
//         )
//
//         val context = object : TestContext() {
//            override suspend fun registerTestCase(nested: NestedTest) {}
//            override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
//            override val testCase: TestCase = testCase
//         }
//         executor.execute(testCase, context, false) {}
//      }
//   }
//}
