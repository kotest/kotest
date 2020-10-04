package io.kotest.engine

import io.kotest.core.TimeoutExecutionContext
import io.kotest.core.internal.TimeoutException
import io.kotest.mpp.NamedThreadFactory
import io.kotest.mpp.log
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ExecutorExecutionContext : TimeoutExecutionContext {

   // we run tests and callbacks inside an executor so that the before/after callbacks
   // and the test itself run on the same thread.
   // @see https://github.com/kotest/kotest/issues/447
   // this cannot be the main thread because we want to continue after a timeout, and
   // we can't interrupt a test doing `while (true) {}`

   override suspend fun <T> executeWithTimeoutInterruption(timeoutInMillis: Long, f: suspend () -> T): T {
      log("Scheduler will interrupt this execution in ${timeoutInMillis}ms")

      val context = coroutineContext

      val scheduler = Executors.newScheduledThreadPool(1, NamedThreadFactory("ExecutionContext-Scheduler-%d"))
      val hasResumed = AtomicBoolean(false)
      return suspendCoroutine { cont ->

         val thisThread = Thread.currentThread()

         // we schedule a task that will resume the coroutine with a timeout exception
         // this task will only fail the coroutine if it has not already returned normally
         scheduler.schedule({
            if (hasResumed.compareAndSet(false, true)) {
               thisThread.interrupt()
               val t = TimeoutException(timeoutInMillis)
               cont.resumeWithException(t)
            }
         }, timeoutInMillis, TimeUnit.MILLISECONDS)
         scheduler.shutdown()

         try {
            // we use the context from the caller, in order to allow context params to propogate down
            // into the test case from test extensions
            // According to the documentation of runBlocking if we give it a context that includes a CoroutineDispatcher,
            // the coroutine will continue to run on the given dispatcher, and the runBlocking will just wait for it to finish.
            // Since we want to be able to interrupt this thread,
            // we would just interrupt the waiting but not the actual execution of the test
            // see https://github.com/kotest/kotest/issues/1725
            runBlocking(context.minusKey(ContinuationInterceptor)) {
               val t = f()
               if (hasResumed.compareAndSet(false, true)) {
                  scheduler.shutdownNow()
                  cont.resume(t)
               }
            }
         } catch (e: AssertionError) {
            if (hasResumed.compareAndSet(false, true)) {
               scheduler.shutdownNow()
               cont.resumeWithException(e)
            }
         } catch (t: Throwable) {
            if (hasResumed.compareAndSet(false, true)) {
               scheduler.shutdownNow()
               cont.resumeWithException(t)
            }
         }
      }
   }
}
