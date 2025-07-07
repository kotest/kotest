package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.interceptors.NextTestExecutionInterceptor
import io.kotest.engine.test.interceptors.TestExecutionInterceptor

internal class FailFastInterceptor(
   private val context: EngineContext,
   private val specContext: SpecContext,
) : TestExecutionInterceptor {

   private val failFastReason = "Failfast enabled"

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      // if a previous test has failed and this test is marked as fail fast, it will be ignored
      val failFast = context.testConfigResolver.failfast(testCase)

      return if (failFast && specContext.testFailed) {
         context.listener.testIgnored(testCase, failFastReason)
         context.testExtensions().ignoredTestListenersInvocation(testCase, failFastReason)
         TestResult.Ignored(failFastReason)
      } else {
         val result = test.invoke(testCase, scope)
         if (result.isErrorOrFailure) specContext.testFailed = true
         result
      }
   }

}
