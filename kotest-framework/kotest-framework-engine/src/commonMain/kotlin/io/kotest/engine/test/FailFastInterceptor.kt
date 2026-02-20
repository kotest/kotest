package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.TestEngineContext
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.interceptors.NextTestExecutionInterceptor
import io.kotest.engine.test.interceptors.TestExecutionInterceptor

internal class FailFastInterceptor(
   private val context: TestEngineContext,
   private val specContext: SpecContext,
) : TestExecutionInterceptor {

   private val failFastReason = "Failfast enabled"

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      val failFast = context.testConfigResolver.failfast(testCase)

      return if (failFast && shouldSkip(testCase)) {
         context.listener.testIgnored(testCase, failFastReason)
         context.testExtensions().ignoredTestListenersInvocation(testCase, failFastReason)
         TestResult.Ignored(failFastReason)
      } else {
         val result = test.invoke(testCase, scope)
         if (result.isErrorOrFailure) markFailure(testCase)
         result
      }
   }

   /**
    * Returns true if a previous failure in the same failfast scope as [testCase] should cause it to be skipped.
    *
    * Spec-level failfast (no enclosing context with `config.failfast=true`) uses a global flag and
    * skips all subsequent tests across the entire spec. Context-level failfast is scoped to the
    * nearest enclosing context that has `config.failfast=true`: only tests within that same context
    * are skipped, so sibling contexts that have not yet had a failure are unaffected.
    */
   private fun shouldSkip(testCase: TestCase): Boolean {
      if (specContext.testFailed) return true
      val scope = failfastScopeOf(testCase) ?: return false
      return scope.descriptor in specContext.failedScopes
   }

   /**
    * Records the failure so that [shouldSkip] can skip subsequent tests in the same scope.
    */
   private fun markFailure(testCase: TestCase) {
      val scope = failfastScopeOf(testCase)
      if (scope != null) {
         specContext.failedScopes.add(scope.descriptor)
      } else {
         specContext.testFailed = true
      }
   }

   /**
    * Returns the nearest test case (self or ancestor) that has [io.kotest.core.test.config.TestConfig.failfast]
    * explicitly set to `true`, or `null` if failfast is coming from the spec or project level.
    */
   private fun failfastScopeOf(testCase: TestCase): TestCase? {
      if (testCase.config?.failfast == true) return testCase
      return testCase.parent?.let { failfastScopeOf(it) }
   }
}
