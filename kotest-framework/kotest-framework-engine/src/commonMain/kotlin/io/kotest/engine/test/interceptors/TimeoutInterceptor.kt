package io.kotest.engine.test.interceptors

import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlin.time.Duration
import kotlin.time.TimeMark

/**
 * A [TestExecutionInterceptor] that installs a general timeout for all invocations of a test.
 */
internal class TimeoutInterceptor(
   private val mark: TimeMark,
   private val testConfigResolver: TestConfigResolver,
) : TestExecutionInterceptor {

   private val logger = Logger(TimeoutInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      val timeout = testConfigResolver.timeout(testCase)

      // This timeout applies to the test itself. If the test has multiple invocations, then
      // this timeout applies across all invocations. In other words, if a test has invocations = 3,
      // each test takes 300ms, and a timeout of 800ms, this would fail, because 3 x 300 > 800.
      logger.log { Pair(testCase.name.name, "Switching context to add timeout $timeout") }

      return try {
         // this will hang on wasm-wasi for reasons
         if (platform != Platform.WasmWasi) {
            withAppropriateTimeout(timeout) {
               test(testCase, scope.withCoroutineContext(coroutineContext))
            }
         } else {
            test(testCase, scope.withCoroutineContext(currentCoroutineContext()))
         }
      } catch (t: CancellationException) {
         if (t is RealTimeTimeoutCancellationException || t is TimeoutCancellationException) {
            logger.log { Pair(testCase.name.name, "Caught timeout $t") }
            val error = TestTimeoutException(timeout, testCase.name.name, t)
            TestResultBuilder.builder().withDuration(mark.elapsedNow()).withError(error).build()
         } else {
            throw t
         }
      }

   }
}

// The implementation copied from Turbine:
// https://github.com/cashapp/turbine/blob/1.1.0/src/commonMain/kotlin/app/cash/turbine/channel.kt#L93
internal suspend fun <T> withAppropriateTimeout(
   timeout: Duration,
   block: suspend CoroutineScope.() -> T,
): T {
   return if (currentCoroutineContext()[TestCoroutineScheduler] != null) {
      // kotlinx.coroutines.withTimeout uses virtual time, so if we try to use that as the mechanism
      // for Kotest's own timeout when using a test coroutine scheduler (eg runTest), it'll fail
      withRealTimeTimeout(timeout, block)
   } else {
      withTimeout(timeout, block)
   }
}

internal suspend fun <T> withAppropriateTimeoutOrNull(
   timeout: Duration,
   block: suspend CoroutineScope.() -> T,
): T? {
   return if (currentCoroutineContext()[TestCoroutineScheduler] != null) {
      // kotlinx.coroutines.withTimeout uses virtual time, so if we try to use that as the mechanism
      // for Kotest's own timeout when using a test coroutine scheduler (eg runTest), it'll fail
      withRealTimeTimeoutOrNull(timeout, block)
   } else {
      withTimeoutOrNull(timeout, block)
   }
}

private suspend fun <T> withRealTimeTimeout(
   timeout: Duration,
   block: suspend CoroutineScope.() -> T,
): T = coroutineScope {
   val blockDeferred = async(start = CoroutineStart.UNDISPATCHED) {
      yield()
      block()
   }

   // Run the timeout on a scope separate from the caller. This ensures that the use of the
   // Default dispatcher doesn't affect the use of a TestScheduler and its fake time.
   @OptIn(DelicateCoroutinesApi::class)
   val timeoutJob = GlobalScope.launch(Dispatchers.Default) { delay(timeout) }

   select {
      blockDeferred.onAwait { result ->
         timeoutJob.cancel()
         result
      }
      timeoutJob.onJoin {
         blockDeferred.cancel()
         throw RealTimeTimeoutCancellationException("Timed out waiting for $timeout")
      }
   }
}

private suspend fun <T> withRealTimeTimeoutOrNull(
   timeout: Duration,
   block: suspend CoroutineScope.() -> T,
): T? = coroutineScope {
   val blockDeferred = async(start = CoroutineStart.UNDISPATCHED) {
      yield()
      block()
   }

   // Run the timeout on a scope separate from the caller. This ensures that the use of the
   // Default dispatcher doesn't affect the use of a TestScheduler and its fake time.
   @OptIn(DelicateCoroutinesApi::class)
   val timeoutJob = GlobalScope.launch(Dispatchers.Default) { delay(timeout) }

   select {
      blockDeferred.onAwait { result ->
         timeoutJob.cancel()
         result
      }
      timeoutJob.onJoin {
         blockDeferred.cancel()
         null
      }
   }
}

// TimeoutCancellationException has an internal constructor, so we need a custom exception to indicate timeout
private class RealTimeTimeoutCancellationException(message: String) : CancellationException(message)

/**
 * Exception used for when a test exceeds its timeout.
 */
open class TestTimeoutException(val timeout: Duration, val testName: String, cause: Throwable? = null) :
   Exception("Test '${testName}' did not complete within $timeout", cause) {

   @Suppress("unused")
   @Deprecated("Maintained for binary compatibility", level = DeprecationLevel.HIDDEN)
   constructor(timeout: Duration, testName: String) : this(timeout, testName, cause = null)
}
