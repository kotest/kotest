package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.Logger
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.TimeMark

// Dispatcher used for jobs to issue the interrupts after timeouts.
// All such jobs share a single daemon thread on the JVM.
@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
private val timeoutDispatcher = newSingleThreadContext("blocking-thread-timeout")

/**
 * If [io.kotest.core.test.config.ResolvedTestConfig.blockingTest] is enabled, then switches the execution
 * to a new thread, so it can be interrupted if the test times out.
 */
@JVMOnly
internal actual fun blockedThreadTimeoutInterceptor(
   configuration: ProjectConfiguration,
   start: TimeMark,
): TestExecutionInterceptor = BlockedThreadTimeoutInterceptor(start)

internal class BlockedThreadTimeoutInterceptor(
   private val start: TimeMark,
) : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return if (testCase.config.blockingTest) {
         // we must switch execution onto a throwaway thread so an interruption
         // doesn't play havoc with a thread in use elsewhere
         val executor = Executors.newSingleThreadExecutor()

         val timeout = testCase.config.timeout
         logger.log { Pair(testCase.name.testName, "this test will time out in $timeout") }

         val timeoutJob = CoroutineScope(coroutineContext).launch(timeoutDispatcher) {
            delay(timeout)
            logger.log { Pair(testCase.name.testName, "Scheduled timeout has hit") }
            executor.shutdownNow()
         }

         try {
            executor.asCoroutineDispatcher().use { dispatcher ->
               withContext(dispatcher) {
                  try {
                     test(testCase, scope.withCoroutineContext(coroutineContext))
                  } finally {
                     timeoutJob.cancel()
                  }
               }
            }
         } catch (t: InterruptedException) {
            logger.log { Pair(testCase.name.testName, "Caught InterruptedException ${t.message}") }
            TestResult.Error(
               start.elapsedNow(),
               BlockedThreadTestTimeoutException(testCase.config.timeout, testCase.name.testName)
            )
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
