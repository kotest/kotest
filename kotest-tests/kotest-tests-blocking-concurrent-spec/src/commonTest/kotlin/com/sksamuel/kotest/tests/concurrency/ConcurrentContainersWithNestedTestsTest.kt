package com.sksamuel.kotest.tests.concurrency

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

// TestExecutionMode.Concurrent only makes
// ROOT-level items (including containers) race for the concurrency slot; tests nested inside the
// SAME container always run sequentially relative to each other (they're invoked by direct
// recursive calls as the container's body registers them, never via a separate launch{}). So the
// interesting concurrent case here is two ROOT containers, each with its own nested child,
// racing with EACH OTHER -- this exercises PinnedTestEngineListener's canOpen() one level deeper
// than a plain root test: the container's own open/close pair must behave exactly like a root
// test's, while its nested child opens/closes safely inside it without contending against the
// unrelated sibling container.
//
// This also reproduces, one level deeper, the console-output-attribution artifact noted in
// TODO of PinnedTestEngineListener/invokeTestEngine.native.kt: whichever container's nested test wins the
// race holds its TC node open for its whole real duration, so the faster sibling's own console
// completion line -- printed for real the moment it actually finishes -- visibly shows up
// attributed to the slower, still-open sibling in Gradle/IntelliJ's per-test output pane.
// pass/fail/timing results are unaffected.
class ConcurrentContainersWithNestedTestsTest : FunSpec({

   testExecutionMode = TestExecutionMode.Concurrent

   context("container with a genuinely blocking nested test") {
      test("a genuinely blocking call in one test") {
         delay(300) // let the polling container start first
         blockCurrentThread(3.seconds)
      }
   }

   context("container with a nested polling test") {
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
         elapsed shouldBeLessThan 2500.milliseconds
         attempts shouldBeGreaterThan 10
      }
   }
})
