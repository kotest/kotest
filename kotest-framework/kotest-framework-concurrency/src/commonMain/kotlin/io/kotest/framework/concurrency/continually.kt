@file:Suppress("DEPRECATION")

package io.kotest.framework.concurrency

import io.kotest.assertions.failure
import io.kotest.common.nonDeterministicTestTimeSource
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
typealias ContinuallyListener<T> = (ContinuallyState<T>) -> Unit

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
data class ContinuallyConfig<T>(
   /** Milliseconds */
   val duration: Long = defaultDuration,
   /** Milliseconds */
   val interval: Interval = defaultInterval,
   /** Milliseconds */
   val initialDelay: Long = defaultDelay,
   val listener: ContinuallyListener<T>? = null,
)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
class ContinuallyBuilder<T> {
   /** Milliseconds */
   var duration: Long = defaultDuration

   /** Milliseconds */
   var interval: Interval = defaultInterval

   /** Milliseconds */
   var initialDelay: Long = defaultDelay
   var listener: ContinuallyListener<T>? = null

   fun toConfig() = ContinuallyConfig(
      duration = duration, interval = interval, initialDelay = initialDelay, listener = listener
   )

   constructor()

   constructor(config: ContinuallyConfig<T>) {
      duration = config.duration
      interval = config.interval
      initialDelay = config.initialDelay
      listener = config.listener
   }
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
data class ContinuallyState<T>(
   val result: T,
   val start: Long,
   val end: Long,
   val times: Int,
)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
private suspend fun <T> ContinuallyConfig<T>.invoke(f: suspend () -> T): T? {
   delay(initialDelay)

   val duration = duration.milliseconds
   val start = nonDeterministicTestTimeSource().markNow()
   val end = start.plus(duration)
   var times = 0
   var result: T? = null

   while (end.hasNotPassedNow()) {
      try {
         result = f()
         listener?.invoke(ContinuallyState(result, 0L, duration.inWholeMilliseconds, times))
      } catch (e: AssertionError) {
         // if this is the first time the check was executed then just rethrow the underlying error
         if (times == 0)
            throw e
         // if not the first attempt then include how many times/for how long the test passed
         throw failure(
            "Test failed after ${start.elapsedNow()}; expected to pass for ${duration}; attempted $times times\nUnderlying failure was: ${e.message}",
            e
         )
      }
      delay(interval.next(++times))
   }
   return result
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> continually(
   config: ContinuallyConfig<T>, configure: ContinuallyBuilder<T>.() -> Unit, @BuilderInference test: suspend () -> T
): T? {
   val builder = ContinuallyBuilder(config).apply(configure)
   return builder.toConfig().invoke(test)
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> continually(
   configure: ContinuallyBuilder<T>.() -> Unit, @BuilderInference test: suspend () -> T
): T? {
   val builder = ContinuallyBuilder<T>().apply(configure)
   return builder.toConfig().invoke(test)
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> continually(duration: Duration, test: suspend () -> T): T? =
   continually(duration.inWholeMilliseconds, test)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> continually(duration: Long, test: suspend () -> T): T? = continually({ this.duration = duration }, test)
