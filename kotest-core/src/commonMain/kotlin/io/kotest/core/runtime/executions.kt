package io.kotest.core.runtime

import io.kotest.core.executeWithAssertionsCheck
import io.kotest.core.executeWithGlobalAssertSoftlyCheck
import io.kotest.core.test.*
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
suspend fun TestCase.executeWithTimeout(context: TestContext, timeout: Duration) {
   // we ensure the timeout is honoured
   withTimeout(timeout.toLongMilliseconds()) {
      // we only run the assertions check for leaf tests
      when (type) {
         TestType.Container -> executeWithGlobalAssertSoftlyCheck { test.invoke(context) }
         TestType.Test -> spec.resolvedAssertionMode().executeWithAssertionsCheck(
            { test.invoke(context) },
            description.name
         )
      }
   }
}
