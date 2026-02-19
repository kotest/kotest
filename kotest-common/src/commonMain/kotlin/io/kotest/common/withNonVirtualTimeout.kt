package io.kotest.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import kotlin.time.Duration

/**
 * Runs [block] with a timeout that never uses virtual time, even if a [TestCoroutineScheduler] is active.
 *
 * When a [TestCoroutineScheduler] is present, [kotlinx.coroutines.withTimeout] uses virtual time,
 * which is not appropriate for non-deterministic functions like `eventually`, or Kotest tests that are
 * meant to wait for real-world events. So this function detects that and then uses our own implementation.
 *
 * If virtual time is not present, then this function will use the regular [kotlinx.coroutines.withTimeout].
 */
@KotestInternal
suspend fun <T> withNonVirtualTimeout(
   timeout: Duration,
   block: suspend CoroutineScope.() -> T,
): T {
   return if (currentCoroutineContext()[TestCoroutineScheduler] != null) {
      withWallClockTimeout(timeout, block)
   } else {
      withTimeout(timeout, block)
   }
}

// The implementation is adapted from the Turbine library
// https://github.com/cashapp/turbine/blob/1.1.0/src/commonMain/kotlin/app/cash/turbine/channel.kt#L93
private suspend fun <T> withWallClockTimeout(
   timeout: Duration,
   block: suspend CoroutineScope.() -> T,
): T = coroutineScope {

   val blockDeferred = async(start = CoroutineStart.UNDISPATCHED) {
      yield()
      block()
   }

   // Run the timeout on a scope separate from the caller. This ensures that the use of the
   // Default dispatcher doesn't affect the use of a TestScheduler and its fake time.
   @OptIn(DelicateCoroutinesApi::class)
   val timeoutJob = GlobalScope.launch(Dispatchers.Default) { delay(timeout) }

   select {
      blockDeferred.onAwait { result ->
         timeoutJob.cancel()
         result
      }
      timeoutJob.onJoin {
         blockDeferred.cancel()
         throw NonDeterministicRealTimeTimeoutCancellationException("Timeout after $timeout")
      }
   }
}

@KotestInternal
class NonDeterministicRealTimeTimeoutCancellationException(message: String) : CancellationException(message)
