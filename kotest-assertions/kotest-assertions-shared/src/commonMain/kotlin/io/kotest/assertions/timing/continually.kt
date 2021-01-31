package io.kotest.assertions.timing

import io.kotest.assertions.SuspendingProducer
import io.kotest.assertions.failure
import io.kotest.assertions.until.Interval
import io.kotest.assertions.until.fixed
import kotlinx.coroutines.delay
import kotlin.time.*

data class ContinuallyState(val start: TimeMark, val end: TimeMark, val times: Int)

fun interface ContinuallyListener<in T> {
   fun onEval(t: T, state: ContinuallyState)

   companion object {
      val noop = ContinuallyListener<Any?> { _, _ -> }
   }
}

data class Continually<T> (
   val duration: Duration = Duration.INFINITE,
   val interval: Interval = 25.milliseconds.fixed(),
   val listener: ContinuallyListener<T> = ContinuallyListener.noop,
   ) {
   suspend operator fun invoke(f: SuspendingProducer<T>): T? {
      val start = TimeSource.Monotonic.markNow()
      val end = start.plus(duration)
      var times = 0
      var result: T? = null
      while (end.hasNotPassedNow()) {
         try {
            result = f()
            listener.onEval(result, ContinuallyState(start, end, times))
         } catch (e: AssertionError) {
            // if this is the first time the check was executed then just rethrow the underlying error
            if (times == 0)
               throw e
            // if not the first attempt then include how many times/for how long the test passed
            throw failure(
               "Test failed after ${start.elapsedNow()}; expected to pass for ${duration.toLongMilliseconds()}ms; attempted $times times\nUnderlying failure was: ${e.message}",
               e
            )
         }
         times++
         delay(interval.next(times))
      }
      return result
   }
}

@Deprecated("Use continually with an interval, using Duration based poll is deprecated",
   ReplaceWith("continually(duration, poll.fixed(), f = f)", "io.kotest.assertions.until.fixed")
)
suspend fun <T> continually(duration: Duration, poll: Duration, f: suspend () -> T) =
   continually(duration, poll.fixed(), f = f)

suspend fun <T> continually(duration: Duration, interval: Interval = 10.milliseconds.fixed(), f: suspend () -> T) =
   Continually<T>(duration, interval).invoke(f)
