package io.kotest.common

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

class WallClockTimeoutTest : FreeSpec() {
   init {
      "wall clock should use wall-clock time even if virtual time is enabled".config(coroutineTestScope = true) {
         shouldThrow<NonDeterministicRealTimeTimeoutCancellationException> {
            withWallClockTimeout(1.milliseconds) {
               delay(1.days)
            }
         }
      }
   }
}
