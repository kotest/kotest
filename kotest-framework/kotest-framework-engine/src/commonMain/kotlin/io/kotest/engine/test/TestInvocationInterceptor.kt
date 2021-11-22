package io.kotest.engine.test

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.interceptors.TestExecutionInterceptor
import io.kotest.mpp.Logger
import io.kotest.mpp.replay
import kotlinx.coroutines.coroutineScope
import kotlin.time.TimeMark

class TestInvocationInterceptor(
   registry: ExtensionRegistry,
   private val timeMark: TimeMark,
) : TestExecutionInterceptor {

   private val extensions = TestExtensions(registry)
   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return try {
         // we wrap in a coroutine scope so that we wait for any user-launched coroutines to finish,
         // and so we can grab any exceptions they throw
         coroutineScope {
            replay(
               testCase.config.invocations,
               testCase.config.threads,
               { extensions.beforeInvocation(testCase, it) },
               { extensions.afterInvocation(testCase, it) }) {
               test(testCase, scope)
            }
         }
         logger.log { Pair(testCase.name.testName, "Test returned without error") }
         TestResult.Success(timeMark.elapsedNow())
      } catch (t: Throwable) {
         logger.log { Pair(testCase.name.testName, "Test threw error $t") }
         createTestResult(timeMark.elapsedNow(), t)
      }
   }
}
