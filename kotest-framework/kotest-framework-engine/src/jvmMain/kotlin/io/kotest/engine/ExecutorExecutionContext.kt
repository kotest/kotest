package io.kotest.engine

import io.kotest.core.ExecutionContext
import io.kotest.core.internal.TimeoutException
import io.kotest.mpp.NamedThreadFactory
import io.kotest.mpp.log
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withTimeout
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * An implementation of [ExecutionContext] that delegates to a single executor thread.
 *
 * In addition, timeout support uses an interrupter thread to detect potential deadlocks in the executor thread.
 *
 * This class is not thread safe and should only be used for a single execution.
 */
class ExecutorExecutionContext : ExecutionContext, AutoCloseable {

   // used to interrupt threads, in case they are deadlocked
   private val interrupter = Executors.newScheduledThreadPool(1, NamedThreadFactory("ExecutionContext-Interrupter"))

   // we will execute tests and callbacks on this thread, so that they are consistently on the same thread
   // See https://github.com/kotest/kotest/issues/447
   private val executor = Executors.newFixedThreadPool(1, NamedThreadFactory("ExecutionContext-Executor"))

   override suspend fun <T> execute(f: suspend () -> T) {
      // we capture the calling context so we can use the keys from it
      val context = coroutineContext

      // We suspend the calling coroutine while we execute this function inside the executor.
      // Once it completes, we'll resume the calling coroutine with the result.
      // This avoids run blocking on the main engine dispatcher
      suspendCoroutine<T> { cont ->
         f.startCoroutine(Continuation(context.plus(executor.asCoroutineDispatcher())) { result ->
            cont.resumeWith(result)
         })
      }
   }

   override suspend fun <T> executeWithTimeout(timeoutInMillis: Long, f: suspend () -> T) {
      withTimeout(timeoutInMillis) {
         val hasResumed = AtomicBoolean(false)

         // the test case may hang, so we need a way to interrupt it after the timeout has expired.
         // withTimeout won't cut it, as that relies on co-operative cancellation, and we need to be able
         // to detect blocked calls, deadlocks and so on. So we grab a reference to the current thread
         // and we'll interrupt that after a time out period using the interruptor service
//      val testThread = Thread.currentThread()
//      log("ExecutorExecutionContext: testThread [${testThread.name}]")

         // we capture the calling context so we can use the keys from it
         val context = coroutineContext

         // We are going to suspend this coroutine and start another coroutine to execute the required function,
         // and this new coroutine will be backed using the thread declared in this execution context.
         // This means we can safely interrupt that thread (to detect timeouts from blocking) because we know
         // that no other coroutine can possibly be using the same thread.
         log("QQQQ about to suspend thread ${Thread.currentThread().name}")
         suspendCoroutine<T> { cont ->

            log("QQQQ about to suspend thread2222 ${Thread.currentThread().name}")

            f.startCoroutine(Continuation(context.plus(executor.asCoroutineDispatcher())) { result ->
               log("QQQQ starting coroutine on thread ${Thread.currentThread().name}")
               // check to make sure we didn't timeout already
               if (hasResumed.compareAndSet(false, true)) {
                  log("ExecutorExecutionContext: Completing coroutine with result $result ${Thread.currentThread().name}")
                  cont.resumeWith(result)
               }
            })

            // we schedule a task that will interrupt the executor thread (and therefore the coroutine) if the
            // function takes longer than the permitted time.
            interrupter.schedule({
               // only interrupt if the function has not already completed normally
               if (hasResumed.compareAndSet(false, true)) {
                  log("ExecutorExecutionContext: Interrupting executor and completing coroutine with timeout ${Thread.currentThread().name}")
                  cont.resumeWithException(TimeoutException(timeoutInMillis))
                  executor.shutdownNow()
               }
            }, timeoutInMillis, TimeUnit.MILLISECONDS)
         }

//      try {
//         coroutineScope {
//            try {
//               log("ExecutorExecutionContext: Executing on thread [${testThread.name}]")
//               f()
//               log("ExecutorExecutionContext: Test completed normally")
//            } catch (t: Throwable) {
//               log("QQQQ $t")
//               throw t
//            }
//         }
//      } catch (t: Throwable) {
//         log("WWWW $t")
//         throw t
//      }
      }
   }

   override fun close() {
      interrupter.shutdown()
      executor.shutdown()
   }
}
