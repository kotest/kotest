package io.kotest.engine.test

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.TestEngineContext
import io.kotest.engine.test.interceptors.NextTestExecutionInterceptor
import io.kotest.engine.test.interceptors.TestExecutionInterceptor
import kotlin.coroutines.coroutineContext

internal class FailFastInterceptor(
   private val context: TestEngineContext,
) : TestExecutionInterceptor {

   private val failFastReason = "Failfast enabled"

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      val failFast = context.testConfigResolver.failfast(testCase)
      if (!failFast) return test.invoke(testCase, scope)

      val tracker = coroutineContext[FailFastScopeTracker]
         ?: return test.invoke(testCase, scope) // no tracker, proceed normally

      val failFastScope = findFailFastScopeDescriptor(testCase)

      return if (tracker.hasFailed(failFastScope)) {
         context.listener.testIgnored(testCase, failFastReason)
         context.testExtensions().ignoredTestListenersInvocation(testCase, failFastReason)
         TestResult.Ignored(failFastReason)
      } else {
         val result = test.invoke(testCase, scope)
         if (result.isErrorOrFailure) tracker.markFailed(failFastScope)
         result
      }
   }

   /**
    * Returns the descriptor of the nearest ancestor test that explicitly has `failfast = true`
    * in its own config (not inherited). Returns `null` if failfast comes from the spec or
    * project configuration rather than from a specific test/context config.
    */
   private fun findFailFastScopeDescriptor(testCase: TestCase): Descriptor.TestDescriptor? {
      var current: TestCase? = testCase
      while (current != null) {
         if (current.config?.failfast == true) return current.descriptor
         current = current.parent
      }
      return null
   }
}
