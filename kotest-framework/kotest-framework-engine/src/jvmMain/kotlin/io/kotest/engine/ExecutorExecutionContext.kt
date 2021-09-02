package io.kotest.engine

import io.kotest.engine.test.TimeoutExecutionContext
import io.kotest.mpp.NamedThreadFactory
import io.kotest.mpp.log
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

/**
 * This element is used to track whether a coroutine is suspended or running, and if running,
 * then the thread it is currently running on.
 */
class CoroutineStatus : ThreadContextElement<Unit> {

   // declare companion object for a key of this element in coroutine context
   companion object Key : CoroutineContext.Key<CoroutineStatus>

   // provide the key of the corresponding context element
   override val key: CoroutineContext.Key<CoroutineStatus> get() = Key

   // if true then this coroutine was suspended at some point
   // all coroutines are initially suspended until they begin
   val suspended: AtomicBoolean = AtomicBoolean(true)

   // if the thread is not null then we know we have been resumed onto a thread
   val thread = AtomicReference<Thread>(null)

   // this is invoked before coroutine is resumed on current thread
   override fun updateThreadContext(context: CoroutineContext) {
      thread.set(Thread.currentThread())
      suspended.set(false)
   }

   // this is invoked after coroutine has suspended on current thread
   override fun restoreThreadContext(context: CoroutineContext, oldState: Unit) {
      thread.set(null)
      suspended.set(true)
   }
}

object ExecutorExecutionContext : TimeoutExecutionContext {

   // we run tests and callbacks inside an executor so that the before/after callbacks
   // and the test itself run on the same thread.
   // @see https://github.com/kotest/kotest/issues/447
   // this cannot be the main thread because we want to continue after a timeout, and
   // we can't interrupt a test doing `while (true) {}`

   // this scheduler is used to interrupt coroutines after timeouts
   private val scheduler =
      Executors.newScheduledThreadPool(1, NamedThreadFactory("ExecutionContext-Scheduler-%d", daemon = true))

   override suspend fun <T> executeWithTimeoutInterruption(timeoutInMillis: Long, f: suspend () -> T): T {

      // the status coroutine context element will track whether the coroutine is suspended or resumed,
      // so we know if we need to interrupt the thread or simply cancel the coroutine
      val status = CoroutineStatus()

      // we schedule a task that will interrupt the coroutine after the timeout has expired
      // this task will use the values in the coroutine status element to know which thread to interrupt
      log { "ExecutorExecutionContext: Scheduler will interrupt this execution in ${timeoutInMillis}ms" }
      val task = scheduler.schedule({
         // if the coroutine is suspended we can cancel using co-operative coroutine cancellation
         // otherwise if it's not suspended, then it's running, and so we need to interrupt
         if (!status.suspended.get()) {
            log { "ExecutorExecutionContext: Interrupting blocked coroutine via thread interruption on thread ${status.thread}" }
            status.thread.get()?.interrupt()
         }
      }, timeoutInMillis, TimeUnit.MILLISECONDS)
      log { "ExecutorExecutionContext: Scheduled task created [${System.identityHashCode(task)}]" }

      // install the status tracker into this coroutine
      // nested tests will install their own tracker, but into a new coroutine, so there is no clash
      // then if this parent is cancelled, it will cancel the children ultimately
      return withContext(status) {
         try {
            f()
         } catch (t: InterruptedException) {
            status.thread.get()?.isInterrupted
            throw TestTimeoutException(timeoutInMillis, "")
         } finally {
            // we must stop the scheduled task from running otherwise it will end up
            // interrupting the thread later when its doing something else
            if (!task.isDone) {
               log { "ExecutorExecutionContext: Cancelling scheduled interupt task ${System.identityHashCode(task)}" }
               task.cancel(false)
            }
         }
      }
   }
}
