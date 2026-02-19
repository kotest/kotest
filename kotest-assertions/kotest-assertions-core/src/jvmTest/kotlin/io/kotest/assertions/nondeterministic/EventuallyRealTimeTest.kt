package io.kotest.assertions.nondeterministic

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Regression tests for https://github.com/kotest/kotest/issues/5147
 *
 * When [coroutineTestScope] is active (which installs a [kotlinx.coroutines.test.TestCoroutineScheduler]),
 * [eventually] must use real (wall-clock) time for its timeout, not virtual time.
 *
 * Without the fix, [eventually] used [kotlinx.coroutines.withTimeout] which respects virtual time
 * when a [kotlinx.coroutines.test.TestCoroutineScheduler] is present. This caused the timeout to
 * fire prematurely: each [eventually] retry calls `delay(25ms)` (advancing virtual time by 25ms),
 * so after 80 iterations virtual time reaches 2 seconds and the timeout fires — even though only
 * a few milliseconds of real time have elapsed. A background job using real I/O would not have
 * had time to complete.
 *
 * Note: [nonDeterministicTestVirtualTimeEnabled] is intentionally NOT set here. When it is set,
 * virtual time is opted in for non-deterministic functions like [eventually], which is the
 * existing behaviour tested in [EventuallyTest].
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class EventuallyRealTimeTest : FunSpec() {

   init {
      coroutineTestScope = true

      test("eventually uses real time when coroutineTestScope is active and nonDeterministicTestVirtualTimeEnabled is not set") {
         val done = AtomicBoolean(false)

         // Perform real I/O on a background thread. Thread.sleep does not advance virtual time,
         // so this completes after ~300 ms of real (wall-clock) time.
         val job = launch(Dispatchers.Default) {
            Thread.sleep(300)
            done.set(true)
         }

         // Before the fix: withTimeout(2.seconds) used virtual time. Each retry's delay(25ms)
         // advances virtual time, so after 80 iterations (80 × 25ms = 2000ms virtual) the timeout
         // fires. Since all 80 iterations ran in near-zero real time, the background job has not
         // yet finished and the assertion always fails.
         //
         // After the fix: withNonDeterministicTimeout(2.seconds) uses real time. The retry loop
         // still runs very quickly (delay is virtual = instant), but the 2-second real timeout
         // keeps running. After ~300ms real time the background job finishes, done becomes true,
         // and eventually succeeds well within the 2-second window.
         eventually(2.seconds) {
            done.get() shouldBe true
         }

         job.cancel()
      }

      test("eventually throws AssertionError when real-time timeout fires") {
         shouldThrow<AssertionError> {
            // This block always fails; the 100ms real-time timeout fires before any success.
            // The important thing is that an AssertionError is thrown (not CancellationException).
            eventually(100.milliseconds) {
               "error" shouldBe "ok"
            }
         }
      }
   }
}
