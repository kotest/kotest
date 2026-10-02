package com.sksamuel.kotest.tests.concurrency

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

// Blocks the current (real, OS) thread for the given duration -- unlike delay(), which suspends
// without occupying a thread. Needed to reproduce a genuinely blocking call (eg Thread.sleep,
// blocking IO) rather than a well-behaved suspension point.
internal expect fun blockCurrentThread(duration: Duration)

// see https://github.com/kotest/kotest/issues/6188 -- with TestExecutionMode.Concurrent, a test
// that makes a genuinely blocking call must not starve the timers (delay/eventually) of another
// concurrently running test in the *same* spec. Unlike SpecExecutionMode.Concurrent (which is
// JVM-only -- see TestSuiteScheduler.concurrency()), TestExecutionMode.Concurrent is not gated to
// any platform, so this spec is expected to exercise the same bug identically on Kotlin/Native.
class BlockingCallDoesNotStarveConcurrentTestTest : FunSpec({

   testExecutionMode = TestExecutionMode.Concurrent

   test("a genuinely blocking call in one test") {
      delay(300) // let the polling test start first
      blockCurrentThread(3.seconds)
   }

   test("should not starve eventually() in a concurrently running test") {
      var attempts = 0
      val start = TimeSource.Monotonic.markNow()

      runCatching {
         eventually(eventuallyConfig {
            duration = 2.seconds
            interval = 100.milliseconds
         }) {
            attempts++
            error("keep polling")
         }
      }

      val elapsed = start.elapsedNow()
      // previously, the blocking test's Thread.sleep-equivalent froze this test's coroutine
      // resumption too, stretching this ~2s window out to ~3s+ and collapsing the attempt count
      elapsed shouldBeLessThan 2500.milliseconds
      attempts shouldBeGreaterThan 10
   }
})
