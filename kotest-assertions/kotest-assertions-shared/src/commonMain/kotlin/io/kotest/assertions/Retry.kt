package io.kotest.assertions

import io.kotest.common.MonotonicTimeSourceCompat
import io.kotest.mpp.bestName
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

/**
 * Retry [f] until it's a success or [maxRetry]/[timeout] is reached
 *
 * This will treat any Exception as a failure, along with [AssertionError].
 *
 * Retry delay might increase exponentially if you choose a [multiplier] value. For example, if you want to configure
 * 5 [maxRetry], with an initial [delay] of 1s between requests, the delay between requests will increase when you
 * choose 2 as your [multiplier]:
 * 1 - Failed (wait 1s before retrying)
 * 2 - Failed (wait 2s before retrying)
 * 3 - Failed (wait 4s before retrying)
 * ..
 *
 * If either timeout or max retries is reached, the execution will be aborted and an exception will be thrown.
 *
 * */
suspend fun <T> retry(
   maxRetry: Int,
   timeout: Duration,
   delay: Duration = 1.seconds,
   multiplier: Int = 1,
   f: suspend () -> T
): T = retry(maxRetry, timeout, delay, multiplier, Exception::class, f)


/**
 * Retry [f] until it's a success or [maxRetry]/[timeout] is reached
 *
 * This will treat only [exceptionClass] as a failure, along with [AssertionError].
 *
 * Retry delay will increase exponentially if you choose a [multiplier] value. For example, if you want to configure
 * 5 [maxRetry], with an initial [delay] of 1s between requests, the delay between requests will increase when you
 * choose 2 as your [multiplier]:
 * 1 - Failed (wait 1s before retrying)
 * 2 - Failed (wait 2s before retrying)
 * 3 - Failed (wait 4s before retrying)
 * ..
 *
 * If either timeout or max retries is reached, the execution will be aborted and an exception will be thrown.
 *
 * */
suspend fun <T> retry(
   maxRetry: Int,
   timeout: Duration,
   delay: Duration = 1.seconds,
   multiplier: Int = 1,
   exceptionClass: KClass<out Throwable>,
   f: suspend () -> T
): T {
   return retry(RetryConfig(maxRetry, timeout, delay, multiplier, exceptionClass), f)
}

suspend fun <T> retry(
   config: RetryConfig,
   test: suspend () -> T
): T {
   val mark = MonotonicTimeSourceCompat.markNow()
   val end = mark.plus(config.timeout)
   var retrySoFar = 0
   var nextAwaitDuration = config.delay.inWholeMilliseconds
   var lastError: Throwable? = null

   while (end.hasNotPassedNow() && retrySoFar < config.maxRetry) {
      try {
         return test()
      } catch (e: Throwable) {
         when {
            // Not the kind of exceptions we were prepared to tolerate
            e::class.simpleName != "AssertionError" &&
               e::class != config.exceptionClass &&
               e::class.bestName() != "org.opentest4j.AssertionFailedError" &&
               !e::class.bestName().endsWith("AssertionFailedError") -> throw e
         }
         lastError = e
         // else ignore and continue
      }
      retrySoFar++
      if (retrySoFar >= config.maxRetry || end.hasPassedNow()) break
      delay(nextAwaitDuration)
      nextAwaitDuration *= config.multiplier
   }
   val underlyingCause = if (lastError == null) "" else "; underlying cause was ${lastError.message}"
   throw failure(
      "Test failed after ${config.delay.toLong(DurationUnit.SECONDS)} seconds; attempted $retrySoFar times$underlyingCause",
      lastError
   )
}

fun retryConfig(@BuilderInference configure: RetryConfigBuilder.() -> Unit): RetryConfig {
   val builder = RetryConfigBuilder()
   builder.configure()
   return builder.build()
}

class RetryConfigBuilder {
   var maxRetry: Int = 1
   var timeout: Duration = Duration.INFINITE
   var delay: Duration = Duration.ZERO
   var multiplier: Int = 1
   var exceptionClass: KClass<out Throwable>? = null
}

internal fun RetryConfigBuilder.build(): RetryConfig {
   return RetryConfig(
      maxRetry = this.maxRetry,
      timeout = this.timeout,
      delay = this.delay,
      multiplier = this.multiplier,
      exceptionClass = this.exceptionClass,
   )
}

data class RetryConfig(
   val maxRetry: Int,
   val timeout: Duration,
   val delay: Duration = 1.seconds,
   val multiplier: Int = 1,
   val exceptionClass: KClass<out Throwable>?,
)
