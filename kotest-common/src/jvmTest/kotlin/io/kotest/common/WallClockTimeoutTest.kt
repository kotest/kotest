package io.kotest.common

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import kotlin.time.Duration.Companion.milliseconds

class WallClockTimeoutTest : FreeSpec() {
   init {
      "withWallClockTimeout should use wall-clock time even if virtual time is enabled".config(coroutineTestScope = true) {
         shouldThrow<NonDeterministicRealTimeTimeoutCancellationException> {
            withWallClockTimeout(1.milliseconds) {
               Thread.sleep(1000)
            }
         }
      }
   }
}
