package io.kotest.engine.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Returns a dispatcher that can be used to launch spec/test coroutines concurrently,
 * so that a blocking test occupies only its own thread instead of starving every other concurrently scheduled spec/test,
 * which would otherwise all share the single-threaded event loop of the runner's top level runBlocking.
 */
internal expect fun concurrentExecutionDispatcher(): CoroutineDispatcher

/**
 * Coroutine context marker indicating that an ancestor coroutine has already switched to a real
 * multi-threaded dispatcher via [concurrentExecutionContext]. Inheriting "no dispatcher" only
 * means "single-threaded" if the ambient context actually still is single-threaded; once an
 * ancestor has switched dispatchers that assumption no longer holds, so a nested scope launching
 * with concurrency == 1 must check for this marker to know whether it needs to explicitly
 * re-confine itself to preserve launch-order == execution-order.
 */
private object ConcurrentSchedulingKey : CoroutineContext.Key<ConcurrentSchedulingElement>
private class ConcurrentSchedulingElement : AbstractCoroutineContextElement(ConcurrentSchedulingKey)

/**
 * Computes the context to launch spec/test coroutines with for the given [concurrency].
 *
 * - concurrency > 1: hand off to the real dispatcher (so tests actually run in parallel), and
 *   mark the hierarchy so nested concurrency == 1 scopes know to confine themselves.
 * - concurrency == 1 but an ancestor already marked the hierarchy as multi-threaded: explicitly
 *   confine to a single worker via [CoroutineDispatcher.limitedParallelism], which serializes
 *   tasks submitted to it in submission order. This must be a single shared instance across every
 *   launch in the same batch (the caller must compute this once per scheduling call and reuse it
 *   for every launch in that batch, not recompute it per launched item, otherwise each launch gets
 *   its own independent single-worker confinement and nothing is serialized relative to the others).
 * - concurrency == 1 and no ancestor marked the hierarchy: [EmptyCoroutineContext], ie behave
 *   exactly as if this function didn't exist -- this is overwhelmingly the common case, and must
 *   stay free of dispatch overhead, since switching dispatcher at all has measurable real-time
 *   cost that can break tests asserting on tight real-time tolerances.
 */
internal suspend fun concurrentExecutionContext(concurrency: Int): CoroutineContext {
   val ancestorIsConcurrent = currentCoroutineContext()[ConcurrentSchedulingKey] != null
   return when {
      concurrency > 1 -> concurrentExecutionDispatcher() + ConcurrentSchedulingElement()
      ancestorIsConcurrent -> concurrentExecutionDispatcher().limitedParallelism(1) + ConcurrentSchedulingElement()
      else -> EmptyCoroutineContext
   }
}
