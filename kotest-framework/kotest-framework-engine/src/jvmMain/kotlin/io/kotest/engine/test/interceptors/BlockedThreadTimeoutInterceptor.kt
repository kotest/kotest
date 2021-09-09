package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.resolvedTimeout
import io.kotest.engine.test.withCoroutineContext
import io.kotest.mpp.NamedThreadFactory
import io.kotest.mpp.log
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// this scheduler is used to issue the interrupts after timeouts
// we only need one in the JVM
private val scheduler =
   Executors.newScheduledThreadPool(1, NamedThreadFactory("BlockedThreadTimeoutInterceptor-%d", daemon = true))

/**
 * If [io.kotest.core.test.TestCaseConfig.timeoutInterruption] is enabled, then switches the execution
 * to a new thread, so it can be interrupted if the test times out.
 */
actual class BlockedThreadTimeoutInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->

      val interruption = testCase.config.timeoutInterruption ?: testCase.spec.timeoutInterruption ?: false
      if (interruption) {

         // we must switch execution onto a throwaway thread so the interruption task
         // doesn't play havok with a thread in use elsewhere
         val executor = Executors.newSingleThreadExecutor()

         val timeoutInMillis = resolvedTimeout(testCase)

         // we schedule a task that will interrupt the coroutine after the timeout has expired
         // this task will use the values in the coroutine status element to know which thread to interrupt
         log { "BlockedThreadTimeoutInterceptor: Scheduler will interrupt this test in ${timeoutInMillis}ms" }
         val task = scheduler.schedule({
            log { "BlockedThreadTimeoutInterceptor: Scheduled timeout has hit" }
            executor.shutdownNow()
         }, timeoutInMillis, TimeUnit.MILLISECONDS)

         try {
            withContext(executor.asCoroutineDispatcher()) {
               test(testCase, context.withCoroutineContext(coroutineContext))
            }
         } catch (t: InterruptedException) {
            log { "BlockedThreadTimeoutInterceptor: Caught InterruptedException ${t.message}" }
            throw TestTimeoutException(timeoutInMillis, "")
         } finally {
            // we should stop the scheduled task from running just to be tidy
            if (!task.isDone) {
               log { "BlockedThreadTimeoutInterceptor: Cancelling scheduled interupt task ${System.identityHashCode(task)}" }
               task.cancel(false)
            }
         }
      } else {
         test(testCase, context)
      }
   }
}


