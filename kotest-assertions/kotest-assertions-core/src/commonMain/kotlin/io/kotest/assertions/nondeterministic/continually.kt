package io.kotest.assertions.nondeterministic

import io.kotest.assertions.failure
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Runs the [test] function continually for the given [duration], failing if an exception is
 * thrown during any invocation.
 *
 * To supply more options to continually, use the overload that accepts a [ContinuallyConfiguration].
 */
suspend fun <T> continually(
   duration: Duration,
   test: suspend () -> T,
): T {
   val config = continuallyConfig<T> { this.duration = duration }
   return continually(config, test)
}

/**
 * Runs the [test] function continually using the given [config], failing if an exception is
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

/**
 * A [ContinuallyListener] is invoked on every invocation with the iteration count and the value.
 */
typealias ContinuallyListener<T> = suspend (Int, T) -> Unit

data class ContinuallyConfiguration<T>(
   val duration: Duration,
   val initialDelay: Duration,
   val intervalFn: DurationFn,
   val listener: ContinuallyListener<T>
)

class ContinuallyConfigurationBuilder<T> {

   /**
    * The total time that the test function will run for.
    */
   var duration: Duration = 5.seconds

   /**
    * The total time that the test function can take to complete successfully.
    */
   var initialDelay: Duration = Duration.ZERO

   /**
    * The delay between invocations. This delay is overriden by the [intervalFn] if that is not null.
    */
   var interval: Duration = 25.milliseconds

   /**
    * A function that is invoked to calculate the next interval. This if this null, then the
    * fixed value of [interval] is used.
    *
    * This function can be used to implement [fibonacci] or [exponential] backoffs.
    */
   var intervalFn: DurationFn? = null

   /**
    * A function that is invoked after each invocation, with the iteration count and current value.
    */
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
