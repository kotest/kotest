package io.kotest.common

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@OptIn(ExperimentalCoroutinesApi::class)
class WallClockTimeoutTest : FreeSpec() {
   init {
      "withWallClockTimeout should use wall-clock time even if virtual time is enabled".config(coroutineTestScope = true) {
         shouldThrow<NonDeterministicRealTimeTimeoutCancellationException> {
            withWallClockTimeout(1.milliseconds) {
               Thread.sleep(1000)
            }
         }
      }

      "withWallClockTimeout should cancel the watchdog coroutine when the block throws" {
         DebugProbes.install()
         try {
            shouldThrow<IllegalStateException> {
               withWallClockTimeout(Duration.INFINITE) {
                  error("boom")
               }
            }
            // the watchdog runs on GlobalScope/Dispatchers.Default, so give it a moment to
            // start in case it was leaked rather than cancelled
            delay(100.milliseconds)
            // cancellation is asynchronous, so poll until any started watchdog has terminated
            fun watchdogs() = DebugProbes.dumpCoroutinesInfo().filter { info ->
               info.lastObservedStackTrace().any { it.className.contains("withWallClockTimeout") }
            }
            val mark = TimeSource.Monotonic.markNow()
            while (watchdogs().isNotEmpty() && mark.elapsedNow() < 5.seconds) {
               delay(20.milliseconds)
            }
            watchdogs().shouldBeEmpty()
         } finally {
            DebugProbes.uninstall()
         }
      }
   }
}
