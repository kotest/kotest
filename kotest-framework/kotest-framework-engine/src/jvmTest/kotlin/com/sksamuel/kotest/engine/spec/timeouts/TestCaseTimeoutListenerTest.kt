package com.sksamuel.kotest.engine.spec.timeouts

import io.kotest.core.listeners.TestListener
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.core.test.TestCaseExecutionListener
import io.kotest.core.internal.TestCaseExecutor
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.toTestResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@Suppress("BlockingMethodInNonBlockingContext")
@ExperimentalTime
class TestCaseTimeoutListenerTest : FunSpec() {

   private var blockingCount = 0
   private var suspendingCount = 0

   init {

      afterSpec {
         blockingCount shouldBe 1
         suspendingCount shouldBe 1
      }

      test("tests which timeout during a blocking operation should still run the 'after test' listeners").config(timeout = 1000.milliseconds) {

         // this listener will flick the flag to true so we know it ran
         val listener = object : TestListener {
            override suspend fun afterTest(testCase: TestCase, result: TestResult) {
               blockingCount += 1
            }
         }

         val testCase = TestCase.test(
            TestCaseTimeoutListenerTest::class.toDescription().appendTest("wibble"),
            this@TestCaseTimeoutListenerTest
         ) {
            Thread.sleep(1000000)
         }.copy(
            config = TestCaseConfig(
               true,
               invocations = 1,
               threads = 1,
               timeout = 125.milliseconds,
               listeners = listOf(listener)
            )
         )

         val context = object : TestContext {
            override suspend fun registerTestCase(nested: NestedTest) {}
            override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
            override val testCase: TestCase = testCase
         }
         val testExecutionListener = object : TestCaseExecutionListener {}
         val executor = TestCaseExecutor(testExecutionListener, ExecutorExecutionContext, {}, ::toTestResult)
         executor.execute(testCase, context)
      }

      test("tests which timeout during a suspending operation should still run the 'after test' listeners").config(
         timeout = 1000.milliseconds
      ) {

         // this listener will flick the flag to true so we know it ran
         val listener = object : TestListener {
            override suspend fun afterTest(testCase: TestCase, result: TestResult) {
               suspendingCount += 1
            }
         }

         val testCase = TestCase.test(
            TestCaseTimeoutListenerTest::class.toDescription().appendTest("wibble"),
            this@TestCaseTimeoutListenerTest
         ) {
            delay(1000000)
         }.copy(
            config = TestCaseConfig(
               true,
               invocations = 1,
               threads = 1,
               timeout = 125.milliseconds,
               listeners = listOf(listener)
            )
         )

         val context = object : TestContext {
            override suspend fun registerTestCase(nested: NestedTest) {}
            override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
            override val testCase: TestCase = testCase
         }
         val testExecutionListener = object : TestCaseExecutionListener {}
         val executor = TestCaseExecutor(testExecutionListener, ExecutorExecutionContext, {}, ::toTestResult)
         executor.execute(testCase, context)
      }
   }
}
