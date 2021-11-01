package io.kotest.engine.test.interceptors

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestExtensions
import io.kotest.mpp.replay
import kotlin.time.TimeMark

/**
 * A [TestExecutionInterceptor] that repeats a test based on the test's invocations setting.
 */
internal class InvocationRepeatInterceptor(
   private val registry: ExtensionRegistry,
   private val timeMark: TimeMark
) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return if (testCase.config.invocations < 2) {
         test(testCase, scope)
      } else {
         replay(
            testCase.config.invocations,
            testCase.config.threads,
            { TestExtensions(registry).beforeInvocation(testCase, it) },
            { TestExtensions(registry).afterInvocation(testCase, it) }) {
            test(testCase, scope)
         }
         TestResult.Success(timeMark.elapsedNow())
      }
   }
}

