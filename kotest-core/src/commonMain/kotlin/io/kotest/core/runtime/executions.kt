package io.kotest.core.runtime

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.Project
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.executeWithAssertionsCheck
import io.kotest.core.test.resolvedAssertionMode
import kotlin.time.ExperimentalTime

internal suspend fun TestCase.executeWithBehaviours(context: TestContext) {
   val fn = test
   wrapTestWithAssertionModeCheck(this) {
      wrapTestWithGlobalAssert {
         fn(context)
      }
   }
}

/**
 * Wraps an execution function checking for assertion mode, if a [TestType.Test] and if enabled.
 */
@OptIn(ExperimentalTime::class)
private suspend fun wrapTestWithAssertionModeCheck(testCase: TestCase, run: suspend () -> Unit) {
   when (testCase.type) {
      TestType.Container -> run()
      TestType.Test -> testCase.spec.resolvedAssertionMode().executeWithAssertionsCheck(testCase.description.name.name) {
         run()
      }
   }
}

/**
 * Wraps an execution function with global assert mode if enabled at the project level.
 */
private suspend fun wrapTestWithGlobalAssert(run: suspend () -> Unit) {
   if (Project.globalAssertSoftly()) {
      assertSoftly {
         run()
      }
   } else {
      run()
   }
}
