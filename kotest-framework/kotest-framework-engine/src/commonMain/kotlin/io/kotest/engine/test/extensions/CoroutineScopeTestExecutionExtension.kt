package io.kotest.engine.test.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.withCoroutineContext
import kotlinx.coroutines.coroutineScope

/**
 * Execute the test case wrapped in a [coroutineScope], so that we wait for any child coroutines launched
 * by the user inside the test function to complete before the engine marks the test as completed.
 */
internal object CoroutineScopeTestExecutionExtension : TestExecutionExtension {
   override suspend fun execute(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      coroutineScope {
         test(testCase, context.withCoroutineContext(coroutineContext))
      }
   }
}
