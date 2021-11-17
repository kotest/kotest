package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class SpecInvocationTimeoutShouldNotApplyToContainersTest : FreeSpec({

   timeout = 1.minutes.inWholeMilliseconds
   invocationTimeout = 50.milliseconds.inWholeMilliseconds

   "invocation timeouts at the spec level should not be applied to containers" - {
      // these inner tests will run 10 times, with 10ms pause each time = 100ms total pause
      // if invocationTimeout is applied to the container, the container would fail
      "suspending inner test".config(invocations = 10) {
         delay(10)
      }
      "blocking inner test".config(invocations = 10) {
         delay(10)
      }
   }
})
