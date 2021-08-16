package io.kotest.engine.test.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.withCoroutineContext
import kotlinx.coroutines.coroutineScope

/**
 * Execute the test case wrapped in a [coroutineScope], so that we wait for any child coroutines
 * inside the user's test to complete before the test is marked completed.
 */
internal object CoroutineScopeTestExecutionExtension : TestExecutionExtension {
   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->
      coroutineScope {
         test(context.withCoroutineContext(coroutineContext))
      }
   }
}
