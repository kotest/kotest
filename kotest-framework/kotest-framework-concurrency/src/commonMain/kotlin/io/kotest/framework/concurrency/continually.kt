package io.kotest.framework.concurrency

import io.kotest.assertions.failure
import io.kotest.common.ExperimentalKotest
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.delay

@ExperimentalKotest
data class ContinuallyState(val start: Millis, val end: Millis, val times: Int)

@ExperimentalKotest
fun interface ContinuallyListener<in T> {
   fun onEval(t: T, state: ContinuallyState)

   companion object {
      val default = ContinuallyListener<Any?> { _, _ -> }
   }
}

@ExperimentalKotest
suspend fun <T> continually(
   duration: Millis,
   interval: Interval = 25L.fixed(),
   listener: ContinuallyListener<T> = ContinuallyListener.default,
   f: ConcurrencyProducer<T>
): T? = continually(PatienceConfig(duration, interval), listener, f)

@ExperimentalKotest
suspend fun <T> continually(
    config: PatienceConfig = PatienceConfig.default,
    listener: ContinuallyListener<T> = ContinuallyListener.default,
    f: ConcurrencyProducer<T>
): T? {
   val start = timeInMillis()
   val end = start + config.duration
   var times = 0
   var result: T? = null

   while (timeInMillis() < end) {
      try {
         result = f()
         listener.onEval(result, ContinuallyState(start, end, times))
      } catch (e: AssertionError) {
         // if this is the first time the check was executed then just rethrow the underlying error
         if (times == 0)
            throw e
         // if not the first attempt then include how many times/for how long the test passed
         throw failure(
            "Test failed after $start; expected to pass for ${config.duration}; attempted $times times\nUnderlying failure was: ${e.message}",
            e
         )
      }
      times++
      delay(config.interval.next(times))
   }
   return result
}
