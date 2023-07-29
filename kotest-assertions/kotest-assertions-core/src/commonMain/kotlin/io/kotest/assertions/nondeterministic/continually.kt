package io.kotest.assertions.nondeterministic

import io.kotest.assertions.failure
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Runs a function [test] continually for the given [duration], failing if an exception is
 * thrown during any invocation.
 *
 * To supply more options to continually, use the overload that accepts an [ContinuallyConfiguration].
 */
suspend fun <T> continually(
   duration: Duration,
   test: suspend () -> T,
): T {
   val config = continuallyConfig<T> { this.duration = duration }
   return continually(config, test)
}

/**
 * Runs a function [test] continually using the given [config], failing if an exception is
 * thrown during any invocation.
 */
suspend fun <T> continually(
   config: ContinuallyConfiguration<T>,
   test: suspend () -> T,
): T {

   delay(config.initialDelay)

   val start = timeInMillis()
   val end = start + config.duration.inWholeMilliseconds
   var iterations = 0
   var result: T? = null

   while (timeInMillis() < end) {
      try {
         result = test()
         config.listener.invoke(iterations, result)
      } catch (e: AssertionError) {
         // if this is the first time the check was executed then just rethrow the underlying error
         if (iterations == 0) throw e
         // if not the first attempt then include how many times/for how long the test passed
         throw failure(
            "Test failed after ${start}ms; expected to pass for ${config.duration}; attempted $iterations times\nUnderlying failure was: ${e.message}",
            e
         )
      }
      delay(config.intervalFn.next(++iterations))
   }

   return result ?: error("No successful result")
}

interface ContinuallyListener<T> {
   suspend fun invoke(iteration: Int, t: T)
}

data class ContinuallyConfiguration<T>(
   val duration: Duration,
   val initialDelay: Duration,
   val intervalFn: DurationFn,
   val listener: ContinuallyListener<T>
)

class ContinuallyConfigurationBuilder<T> {
   var duration: Duration = Duration.INFINITE
   var initialDelay: Duration = Duration.ZERO
   var interval: Duration = 25.milliseconds
   var intervalFn: DurationFn? = null
   var listener: ContinuallyListener<T>? = null
}

fun <T> continuallyConfig(
   configure: ContinuallyConfigurationBuilder<T>.() -> Unit,
): ContinuallyConfiguration<T> {
   val config = ContinuallyConfigurationBuilder<T>()
   config.configure()
   return config.build()
}

private fun <T> ContinuallyConfigurationBuilder<T>.build(): ContinuallyConfiguration<T> {
   return ContinuallyConfiguration(
      duration = this.duration,
      initialDelay = this.initialDelay,
      intervalFn = this.intervalFn ?: DurationFn { interval },
      listener = this.listener ?: object : ContinuallyListener<T> {
         override suspend fun invoke(iteration: Int, t: T) {}
      },
   )
}
