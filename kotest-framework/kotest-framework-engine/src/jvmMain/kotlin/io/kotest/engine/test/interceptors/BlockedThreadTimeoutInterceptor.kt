package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.TestResultBuilder
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
 * If [blockingTest] is enabled, then switches the execution
 * to a new thread, so it can be interrupted if the test times out.
 */
@JVMOnly
internal actual fun blockedThreadTimeoutInterceptor(
   start: TimeMark,
   testConfigResolver: TestConfigResolver,
): TestExecutionInterceptor = BlockedThreadTimeoutInterceptor(start, testConfigResolver)

internal class BlockedThreadTimeoutInterceptor(
   private val start: TimeMark,
   private val testConfigResolver: TestConfigResolver,
) : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return if (testConfigResolver.blockingTest(testCase)) {
         // we must switch execution onto a throwaway thread so an interruption
         // doesn't play havoc with a thread in use elsewhere
         val executor = Executors.newSingleThreadExecutor()

         val timeout = testConfigResolver.timeout(testCase)
         logger.log { Pair(testCase.name.name, "this test will time out in $timeout") }

         if (timeout.inWholeMilliseconds <= 1)
            error("Cannot set a blocked thread timeout <= 1ms")

         // this is a separate job that will run on the timeout dispatcher that will shutdown the executor
         // after the timeout has hit.
         @OptIn(ExperimentalCoroutinesApi::class)
         val timeoutJob = CoroutineScope(coroutineContext).launch(timeoutDispatcher) {
            delay(timeout)
            logger.log { Pair(testCase.name.name, "Scheduled timeout has hit") }
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
            logger.log { Pair(testCase.name.name, "Caught InterruptedException ${t.message}") }
            val error = BlockedThreadTestTimeoutException(testConfigResolver.timeout(testCase), testCase.name.name, t)
            TestResultBuilder.builder().withDuration(start.elapsedNow()).withError(error).build()
         }
      } else {
         test(testCase, scope)
      }
   }
}

/**
 * Exception used for when a test exceeds its timeout.
 */
class BlockedThreadTestTimeoutException(timeout: Duration, testName: String, cause: Throwable? = null) :
   TestTimeoutException(timeout, testName, cause) {

   @Suppress("unused")
   @Deprecated("Maintained for binary compatibility", level = DeprecationLevel.HIDDEN)
   constructor(timeout: Duration, testName: String) : this(timeout, testName, cause = null)
}
