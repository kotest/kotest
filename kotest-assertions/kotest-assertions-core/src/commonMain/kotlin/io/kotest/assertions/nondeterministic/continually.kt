package io.kotest.assertions.nondeterministic

import io.kotest.assertions.failure
import io.kotest.assertions.nondeterministic.ContinuallyTimeSource.Companion.getContinuallyTimeSource
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

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

   val start = getContinuallyTimeSource().markNow()
   val end = start.plus(config.duration)
   var iterations = 0
   var result: Result<T> = Result.failure(IllegalStateException("No successful result"))

   while (end.hasNotPassedNow()) {
      runCatching {
         test()
      }.onSuccess {
         result = Result.success(it)
         config.listener.invoke(iterations, it)
      }.onFailure { ex ->
         when (ex) {
            is AssertionError -> {
               if (iterations == 0) throw ex
               throw failure(
                  message = "Test failed after ${start.elapsedNow()}; " +
                     "expected to pass for ${config.duration}; " +
                     "attempted $iterations times\n" +
                     "Underlying failure was: ${ex.message}",
                  cause = ex
               )
            }

            else -> throw ex
         }
      }
      delay(config.intervalFn.next(++iterations))
   }

   return result.getOrThrow()
}

/**
 * A [ContinuallyListener] is invoked on every invocation with the iteration count and the value.
 */
typealias ContinuallyListener<T> = suspend (Int, T) -> Unit

data class ContinuallyConfiguration<T>(
   val duration: Duration,
   val initialDelay: Duration,
   val intervalFn: DurationFn,
   val listener: ContinuallyListener<T>,
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
    * The delay between invocations. This delay is overridden by the [intervalFn] if that is not `null`.
    */
   var interval: Duration = 25.milliseconds

   /**
    * A function that is invoked to calculate the next interval. This if this null, then the
    * fixed value of [interval] is used.
    *
    * This function can be used to implement [fibonacci] or [exponential] backoff.
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


/**
 * Store the [TimeSource] used by [continually].
 *
 * @see getContinuallyTimeSource
 */
internal class ContinuallyTimeSource(
   val timeSource: TimeSource
) : CoroutineContext.Element {

   override val key: CoroutineContext.Key<ContinuallyTimeSource>
      get() = KEY

   internal companion object {
      /**
       * Retrieves the [TimeSource] used by [continually].
       *
       * For internal Kotest testing purposes the [TimeSource] can be overridden.
       * For normal usage [TimeSource.Monotonic] is used.
       */
      internal suspend fun getContinuallyTimeSource(): TimeSource =
         coroutineContext[KEY]?.timeSource
            ?: TimeSource.Monotonic

      internal val KEY = object : CoroutineContext.Key<ContinuallyTimeSource> {}
   }
}
