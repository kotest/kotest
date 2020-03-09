package io.kotest.core.runtime

import io.kotest.core.internal.NamedThreadFactory
import io.kotest.mpp.log
import io.kotest.fp.Try
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class ExecutorExecutionContext : ExecutionContext {

   // we run tests and callbacks inside an executor so that the before/after callbacks
   // and the test itself run on the same thread.
   // @see https://github.com/kotlintest/kotlintest/issues/447
   // this cannot be the main thread because we want to continue after a timeout, and
   // we can't interrupt a test doing `while (true) {}`
   private val executor = Executors.newSingleThreadExecutor(NamedThreadFactory("ExecutionContext-Worker-%d"))

   // used to intercept for timeouts
   private val scheduler = Executors.newScheduledThreadPool(1, NamedThreadFactory("ExecutionContext-Scheduler-%d"))

   override fun close() {
      executor.shutdown()
      scheduler.shutdownNow()
   }

   override suspend fun <T> execute(f: suspend () -> T): Try<T> = Try {
      if (executor.isShutdown) {
         f()
      } else {
         suspendCoroutine { cont ->
            executor.submit {
               try {
                  runBlocking {
                     cont.resume(f())
                  }
               } catch (e: Throwable) {
                  cont.resumeWithException(e)
               }
            }
         }
      }
   }

   @OptIn(ExperimentalTime::class)
   override suspend fun <T> executeWithTimeoutInterruption(timeout: Duration, f: suspend () -> T): T {
      log("Scheduler will interrupt this execution in ${timeout}ms")
      val hasResumed = AtomicBoolean(false)
      return suspendCoroutine { cont ->

         scheduler.schedule({
            if (hasResumed.compareAndSet(false, true)) {
               val t = TimeoutException(timeout)
               cont.resumeWithException(t)
            }
            executor.shutdownNow()
         }, timeout.toLongMilliseconds(), TimeUnit.MILLISECONDS)

         executor.submit {
            try {
               val t = runBlocking { f() }
               if (hasResumed.compareAndSet(false, true))
                  cont.resume(t)
            } catch (e: AssertionError) {
               if (hasResumed.compareAndSet(false, true))
                  cont.resumeWithException(e)
            } catch (t: Throwable) {
               if (hasResumed.compareAndSet(false, true))
                  cont.resumeWithException(t)
            }
         }
      }
   }
}
