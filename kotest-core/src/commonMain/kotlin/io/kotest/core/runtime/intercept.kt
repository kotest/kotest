package io.kotest.core.runtime

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Recursively runs the given [TestCaseExtension]s until no extensions are left.
 * Each extension must invoke the callback given to it, or the test will hang.
 */
suspend fun runExtensions(
   testCase: TestCase,
   extensions: List<TestCaseExtension>,
   action: suspend (TestCase) -> TestResult,
   onComplete: suspend (TestResult) -> Unit
) {
   when {
      extensions.isEmpty() -> {
         val result = action(testCase)
         onComplete(result)
      }
      else -> {
         extensions.first().intercept(
            testCase,
            { test, callback -> runExtensions(test, extensions.drop(1), action, callback) },
            { onComplete(it) }
         )
      }
   }
}
