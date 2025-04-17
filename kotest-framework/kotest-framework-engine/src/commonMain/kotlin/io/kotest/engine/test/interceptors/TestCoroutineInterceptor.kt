package io.kotest.engine.test.interceptors

import io.kotest.common.NonDeterministicTestVirtualTimeEnabled
import io.kotest.common.testCoroutineSchedulerOrNull
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.coroutines.TestScopeElement
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A [TestExecutionInterceptor] that uses [runTest] from the coroutine library
 * to install test dispatchers.
 *
 * This setting cannot be nested.
 */
internal class TestCoroutineInterceptor(private val testConfigResolver: TestConfigResolver) : TestExecutionInterceptor {

   private val logger = Logger(TestCoroutineInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      var result: TestResult = TestResult.Ignored()
      logger.log { Pair(testCase.name.name, "Switching context to coroutines runTest") }

      // Handle timeouts here to avoid the influence of the default timeout set inside runTest
      runTest(
         context = scope.coroutineContext.testCoroutineSchedulerOrNull ?: EmptyCoroutineContext,
         timeout = testConfigResolver.timeout(testCase)
      ) {
         var additionalContext: CoroutineContext = TestScopeElement(this)
         if (testCase.spec.nonDeterministicTestVirtualTimeEnabled) {
            additionalContext += NonDeterministicTestVirtualTimeEnabled
         }
         withContext(additionalContext) {
            result = test(testCase, scope.withCoroutineContext(coroutineContext))
         }
      }
      return result
   }
}
