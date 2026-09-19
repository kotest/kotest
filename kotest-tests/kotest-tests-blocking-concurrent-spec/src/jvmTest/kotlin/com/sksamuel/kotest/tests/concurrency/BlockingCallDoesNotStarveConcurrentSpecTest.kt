package com.sksamuel.kotest.tests.concurrency

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

// with SpecExecutionMode.Concurrent, a spec that
// makes a genuinely blocking call must not starve the timers (delay/eventually) of other concurrently
// running specs, since they are scheduled onto their own dispatcher rather than sharing one thread.
// This module is isolated (rather than added to kotest-tests-concurrency-specs) because the multi-second
// Thread.sleep below would otherwise break that module's own project-wide timing assertions.
@EnabledIf(LinuxOnlyGithubCondition::class)
class BlockingConcurrentSpec : FunSpec({
   test("a genuinely blocking call in one spec") {
      // let PollingConcurrentSpec start polling first
      delay(300)
      @Suppress("BlockingMethodInNonBlockingContext")
      Thread.sleep(3000)
   }
})

@EnabledIf(LinuxOnlyGithubCondition::class)
class PollingConcurrentSpec : FunSpec({
   test("should not starve eventually() in a concurrently running spec") {
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
      // previously, BlockingConcurrentSpec's Thread.sleep froze this spec's coroutine resumption too,
      // stretching this ~2s window out to ~3s+ and collapsing the attempt count to a handful
      elapsed shouldBeLessThan 2500.milliseconds
      attempts shouldBeGreaterThan 10
   }
})
