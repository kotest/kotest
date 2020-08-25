package io.kotest.mpp

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.*

actual suspend fun replay(
   times: Int,
   threads: Int,
   before: suspend (Int) -> Unit,
   after: suspend (Int) -> Unit,
   action: suspend (Int) -> Unit
) {
   if (threads == 1) {
      repeat(times) {
         before(it)
         action(it)
         after(it)
      }
   } else {
      val executor = Executors.newFixedThreadPool(threads, NamedThreadFactory("replay-%d"))
      val ctx = ExecutorServiceContext(executor)
      val error = AtomicReference<Throwable>(null)
      for (k in 0 until times) {
         future(ctx) {
            try {
               before(k)
               action(k)
            } catch (t: Throwable) {
               error.compareAndSet(null, t)
            } finally {
               after(k)
            }
         }
      }
      executor.shutdown()
      executor.awaitTermination(1, TimeUnit.DAYS)

      if (error.get() != null)
         throw error.get()
   }
}

/**
 * Wraps an [ExecutorService] in a [CoroutineContext] as a [ContinuationInterceptor]
 * scheduling on the [ExecutorService] when [kotlin.coroutines.intrinsics.intercepted] is called.
 */
private class ExecutorServiceContext(val pool: ExecutorService) :
   AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
   override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
      ExecutorServiceContinuation(pool, continuation.context.fold(continuation) { cont, element ->
         if (element != this@ExecutorServiceContext && element is ContinuationInterceptor)
            element.interceptContinuation(cont) else cont
      })
}

/** Wrap existing continuation to resumes itself on the provided [ExecutorService] */
private class ExecutorServiceContinuation<T>(val pool: ExecutorService, val cont: Continuation<T>) : Continuation<T> {
   override val context: CoroutineContext = cont.context

   override fun resumeWith(result: Result<T>) {
      pool.execute { cont.resumeWith(result) }
   }
}

private fun <A> future(ctx: CoroutineContext, f: suspend () -> A): Future<A> =
   CompletableFuture<A>().apply {
      f.startCoroutine(Continuation(ctx) { res ->
         res.fold(::complete, ::completeExceptionally)
      })
   }
