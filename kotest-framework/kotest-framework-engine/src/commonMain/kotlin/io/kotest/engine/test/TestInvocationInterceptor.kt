package io.kotest.engine.test

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.interceptors.TestExecutionInterceptor
import io.kotest.core.Logger
import io.kotest.engine.concurrency.replay
import kotlinx.coroutines.coroutineScope
import kotlin.time.Duration
import kotlin.time.TimeMark

internal class TestInvocationInterceptor(
   registry: ExtensionRegistry,
   private val timeMark: TimeMark,
   private val invocationInterceptors: List<TestExecutionInterceptor>,
) : TestExecutionInterceptor {

   private val extensions = TestExtensions(registry)
   private val logger = Logger(TestInvocationInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult,
   ): TestResult {
      return try {
         // we wrap in a coroutine scope so that we wait for any user-launched coroutines to finish,
         // and so we can grab any exceptions they throw
         coroutineScope {
            replay(
               testCase.config.invocations,
               testCase.config.threads
            )
            { runBeforeTestAfter(testCase, scope, it, test) }
         }
         logger.log { Pair(testCase.name.testName, "Test returned without error") }
         try {
            TestResult.Success(timeMark.elapsedNow())
         } catch (e: Throwable) {
            TestResult.Success(Duration.ZERO) // workaround for kotlin 1.5
         }
      } catch (t: Throwable) {
         logger.log { Pair(testCase.name.testName, "Test threw error $t") }
         try {
            createTestResult(timeMark.elapsedNow(), t)
         } catch (e: Throwable) {
            TestResult.Error(Duration.ZERO, t) // workaround for kotlin 1.5
         }
      }
   }

   private suspend fun runBeforeTestAfter(
      testCase: TestCase,
      scope: TestScope,
      times: Int,
      test: suspend (TestCase, TestScope) -> TestResult,
   ) {
      val executeWithBeforeAfter: suspend (TestCase, TestScope) -> TestResult = { tc, sc ->
         try {
            extensions.beforeInvocation(tc, times)
            test(tc, sc)
         } finally {
            extensions.afterInvocation(tc, times)
         }
      }

      val wrappedTest = invocationInterceptors.foldRight(executeWithBeforeAfter) { ext, fn ->
         { tc, sc -> ext.intercept(tc, sc, fn) }
      }

      wrappedTest(testCase, scope)
   }
}
