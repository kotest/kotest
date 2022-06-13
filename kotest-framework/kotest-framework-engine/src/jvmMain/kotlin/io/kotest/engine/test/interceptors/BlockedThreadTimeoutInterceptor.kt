package io.kotest.engine.test.interceptors

import io.kotest.common.TimeMarkCompat
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import io.kotest.mpp.NamedThreadFactory
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

// this scheduler is used to issue the interrupts after timeouts
// we only need one in the JVM
private val scheduler =
   Executors.newScheduledThreadPool(1, NamedThreadFactory("BlockedThreadTimeoutInterceptor-%d", daemon = true))

/**
 * If [io.kotest.core.test.TestCaseConfig.blockingTest] is enabled, then switches the execution
 * to a new thread, so it can be interrupted if the test times out.
 */
internal actual fun blockedThreadTimeoutInterceptor(
   configuration: ProjectConfiguration,
   start: TimeMarkCompat,
): TestExecutionInterceptor = BlockedThreadTimeoutInterceptor(start)

internal class BlockedThreadTimeoutInterceptor(
   private val start: TimeMarkCompat,
) : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return if (testCase.config.blockingTest) {

         // we must switch execution onto a throwaway thread so the interruption task
         // doesn't play havok with a thread in use elsewhere
         val executor = Executors.newSingleThreadExecutor()

         // we schedule a task that will interrupt the coroutine after the timeout has expired
         // this task will use the values in the coroutine status element to know which thread to interrupt
         logger.log { Pair(testCase.name.testName, "Scheduler will interrupt this test in ${testCase.config.timeout}") }
         val task = scheduler.schedule({
            logger.log { Pair(testCase.name.testName, "Scheduled timeout has hit") }
            executor.shutdownNow()
         }, testCase.config.timeout?.inWholeMilliseconds ?: 10000000000L, TimeUnit.MILLISECONDS)

         try {
            withContext(executor.asCoroutineDispatcher()) {
               test(testCase, scope.withCoroutineContext(coroutineContext))
            }
         } catch (t: InterruptedException) {
            logger.log { Pair(testCase.name.testName, "Caught InterruptedException ${t.message}") }
            TestResult.Error(
               start.elapsedNow(),
               BlockedThreadTestTimeoutException(testCase.config.timeout ?: Duration.INFINITE, testCase.name.testName)
            )
         } finally {
            // we should stop the scheduled task from running just to be tidy
            if (!task.isDone) {
               logger.log { Pair(testCase.name.testName, "Cancelling scheduled task ${System.identityHashCode(task)}") }
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
