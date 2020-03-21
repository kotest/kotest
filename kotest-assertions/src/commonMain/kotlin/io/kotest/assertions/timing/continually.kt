package io.kotest.assertions.timing

import io.kotest.assertions.failure
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
suspend fun <T> continually(duration: Duration, f: suspend () -> T): T? = continually(duration, 10.milliseconds, f)

@OptIn(ExperimentalTime::class)
suspend fun <T> continually(duration: Duration, poll: Duration, f: suspend () -> T): T? {
   val mark = TimeSource.Monotonic.markNow()
   val end = mark.plus(duration)
   var times = 0
   var result: T? = null
   while (end.hasNotPassedNow()) {
      try {
         result = f()
      } catch (e: AssertionError) {
         // if this is the first time the check was executed then just rethrow the underlying error
         if (times == 0)
            throw e
         // if not the first attempt then include how many times/for how long the test passed
         throw failure(
            "Test failed after ${mark.elapsedNow()
               .toLongMilliseconds()}ms; expected to pass for ${duration.toLongMilliseconds()}ms; attempted $times times\nUnderlying failure was: ${e.message}",
            e
         )
      }
      times++
      delay(poll.toLongMilliseconds())
   }
   return result
}
