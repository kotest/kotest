package io.kotest.framework.concurrency

import io.kotest.assertions.failure
import io.kotest.common.ExperimentalKotest
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.delay

@ExperimentalKotest
sealed class ContinuallyConfig<out T> {
   abstract val patience: PatienceConfig
}

@ExperimentalKotest
data class BasicContinuallyConfig(
   override val patience: PatienceConfig = PatienceConfig(),
) : ContinuallyConfig<Nothing>() {
   constructor(
      duration: Long,
      interval: Interval = PatienceConfig.defaultInterval,
   ) : this(PatienceConfig(duration, interval))

   fun withDuration(duration: Long) = copy(patience = patience.copy(duration = duration))
   suspend fun <T> withDuration(duration: Long, f: suspend () -> T): T? = withDuration(duration).invoke(f)

   fun withInterval(interval: Interval) = copy(patience = patience.copy(interval = interval))
   suspend fun <T> withInterval(interval: Interval, f: suspend () -> T): T? = withInterval(interval).invoke(f)

   fun <T> withListener(listener: ContinuallyListener<T>) = GenericContinuallyConfig(patience = patience, listener = listener)
   suspend fun <T> withListener(listener: ContinuallyListener<T>, f: suspend () -> T): T? = withListener(listener).invoke(f)
}

@ExperimentalKotest
data class GenericContinuallyConfig<T>(
   override val patience: PatienceConfig = PatienceConfig(),
   val listener: ContinuallyListener<T>? = null
) : ContinuallyConfig<T>() {
   constructor(duration: Long, interval: Interval = PatienceConfig.defaultInterval, listener: ContinuallyListener<T>? = null)
      : this(PatienceConfig(duration, interval), listener)

   fun withDuration(duration: Long) = copy(patience = patience.copy(duration = duration))
   suspend fun withDuration(duration: Long, f: suspend () -> T): T? = withDuration(duration).invoke(f)

   fun withInterval(interval: Interval) = copy(patience = patience.copy(interval = interval))
   suspend fun withInterval(interval: Interval, f: suspend () -> T): T? = withInterval(interval).invoke(f)

   fun withListener(listener: ContinuallyListener<T>) = copy(listener = listener)
   suspend fun withListener(listener: ContinuallyListener<T>, f: suspend () -> T): T? = withListener(listener).invoke(f)

}

@ExperimentalKotest
data class ContinuallyState(val start: Long, val end: Long, val times: Int)

@ExperimentalKotest
fun interface ContinuallyListener<in T> {
   fun onEval(t: T, state: ContinuallyState)

   companion object {
      val default = ContinuallyListener<Any?> { _, _ -> }
   }
}

@ExperimentalKotest
fun continually(duration: Long) = BasicContinuallyConfig(duration)

@ExperimentalKotest
suspend fun <T> continually(duration: Long, f: suspend () -> T) = continually(duration).invoke(f)

@ExperimentalKotest
suspend fun <T> ContinuallyConfig<T>.invoke(f: suspend () -> T): T? {
   val start = timeInMillis()
   val end = start + patience.duration
   var times = 0
   var result: T? = null

   while (timeInMillis() < end) {
      try {
         result = f()

         when (this) {
            is BasicContinuallyConfig -> Unit
            is GenericContinuallyConfig -> listener?.onEval(result, ContinuallyState(start, end, times))
         }
      } catch (e: AssertionError) {
         // if this is the first time the check was executed then just rethrow the underlying error
         if (times == 0)
            throw e
         // if not the first attempt then include how many times/for how long the test passed
         throw failure(
            "Test failed after $start; expected to pass for ${patience.duration}; attempted $times times\nUnderlying failure was: ${e.message}",
            e
         )
      }
      times++
      delay(patience.interval.next(times))
   }
   return result
}
