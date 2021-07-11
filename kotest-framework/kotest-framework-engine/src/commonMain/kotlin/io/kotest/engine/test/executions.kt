package io.kotest.engine.test

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

internal suspend fun TestCase.executeWithBehaviours(context: TestContext) {
   val fn = test
   wrapTestWithAssertionModeCheck(this) {
      wrapTestWithGlobalAssert(this) {
         fn(context)
      }
   }
}

/**
 * Wraps an execution function checking for assertion mode, if a [TestType.Test] and if enabled.
 */
private suspend fun wrapTestWithAssertionModeCheck(testCase: TestCase, run: suspend () -> Unit) {
   when (testCase.type) {
      TestType.Container -> run()
      TestType.Test -> testCase.spec.resolvedAssertionMode().executeWithAssertionsCheck(testCase.description) {
         run()
      }
   }
}

/**
 * Wraps an execution function with global assert mode if enabled at the project level and if
 * this [testCase] is a [TestType.Test].
 */
private suspend fun wrapTestWithGlobalAssert(testCase: TestCase, run: suspend () -> Unit) {
   when {
      testCase.type == TestType.Test && configuration.globalAssertSoftly -> assertSoftly {
         run()
      }
      else -> run()
   }
}
