package io.kotest.engine.test.interceptors

import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.resolvedTimeout
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.NamedThreadFactory
import io.kotest.mpp.log
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.milliseconds

// this scheduler is used to issue the interrupts after timeouts
// we only need one in the JVM
private val scheduler =
   Executors.newScheduledThreadPool(1, NamedThreadFactory("BlockedThreadTimeoutInterceptor-%d", daemon = true))

/**
 * If [io.kotest.core.test.TestCaseConfig.blockingTest] is enabled, then switches the execution
 * to a new thread, so it can be interrupted if the test times out.
 */
internal actual fun blockedThreadTimeoutInterceptor(configuration: Configuration): TestExecutionInterceptor =
   BlockedThreadTimeoutInterceptor(configuration)

internal class BlockedThreadTimeoutInterceptor(private val configuration: Configuration) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return if (testCase.config.blockingTest) {

         // we must switch execution onto a throwaway thread so the interruption task
         // doesn't play havok with a thread in use elsewhere
         val executor = Executors.newSingleThreadExecutor()

         val timeout = resolvedTimeout(testCase, configuration.timeout.milliseconds)

         // we schedule a task that will interrupt the coroutine after the timeout has expired
         // this task will use the values in the coroutine status element to know which thread to interrupt
         log { "BlockedThreadTimeoutInterceptor: Scheduler will interrupt this test in $timeout" }
         val task = scheduler.schedule({
            log { "BlockedThreadTimeoutInterceptor: Scheduled timeout has hit" }
            executor.shutdownNow()
         }, timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)

         try {
            withContext(executor.asCoroutineDispatcher()) {
               test(testCase, scope.withCoroutineContext(coroutineContext))
            }
         } catch (t: InterruptedException) {
            log { "BlockedThreadTimeoutInterceptor: Caught InterruptedException ${t.message}" }
            throw BlockedThreadTestTimeoutException(timeout, testCase.name.testName)
         } finally {
            // we should stop the scheduled task from running just to be tidy
            if (!task.isDone) {
               log { "BlockedThreadTimeoutInterceptor: Cancelling scheduled interupt task ${System.identityHashCode(task)}" }
               task.cancel(false)
            }
         }
      } else {
         test(testCase, scope)
      }
   }
}

/**
 * Exception used for when a test exceeds its timeout.
 */
class BlockedThreadTestTimeoutException(timeout: Duration, testName: String) : TestTimeoutException(timeout, testName)
