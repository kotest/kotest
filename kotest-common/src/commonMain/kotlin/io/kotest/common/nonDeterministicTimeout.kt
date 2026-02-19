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
 * Runs [block] with a timeout, using real (wall-clock) time if a [TestCoroutineScheduler] is active
 * but virtual time has not been explicitly enabled for non-deterministic functions via
 * [NonDeterministicTestVirtualTimeEnabled].
 *
 * This mirrors the logic of [nonDeterministicTestTimeSource]: virtual time is only used when
 * both a [TestCoroutineScheduler] is present AND [NonDeterministicTestVirtualTimeEnabled] is in
 * the coroutine context.
 */
@KotestInternal
suspend fun <T> withNonDeterministicTimeout(
   timeout: Duration,
   block: suspend CoroutineScope.() -> T,
): T {
   val context = currentCoroutineContext()
   return if (context[TestCoroutineScheduler] != null && context[NonDeterministicTestVirtualTimeEnabled] == null) {
      withNonDeterministicRealTimeTimeout(timeout, block)
   } else {
      withTimeout(timeout, block)
   }
}

// The implementation is adapted from the Turbine library and kotest's TimeoutInterceptor:
// https://github.com/cashapp/turbine/blob/1.1.0/src/commonMain/kotlin/app/cash/turbine/channel.kt#L93
private suspend fun <T> withNonDeterministicRealTimeTimeout(
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
         throw NonDeterministicRealTimeTimeoutCancellationException("eventually timed out after $timeout")
      }
   }
}

@KotestInternal
class NonDeterministicRealTimeTimeoutCancellationException(message: String) : CancellationException(message)
