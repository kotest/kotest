package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.delay
import kotlin.time.milliseconds
import kotlin.time.minutes

class SpecLevelInvocationTimeoutContainerTest : FreeSpec({

   timeout = 1.minutes.toLongMilliseconds()
   invocationTimeout = 50.milliseconds.toLongMilliseconds()

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
