package io.kotest.framework.concurrency

import io.kotest.assertions.failure
import io.kotest.common.ExperimentalKotest
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.delay

@ExperimentalKotest
fun interface UntilListener<in T> {
   fun onEval(t: T): Boolean

   companion object {
      val default = UntilListener<Any?> { true }
   }
}

@ExperimentalKotest
suspend fun until(
   duration: Millis,
   interval: Interval = 25L.fixed(),
   booleanProducer: ConcurrencyProducer<Boolean>
) {
   until(PatienceConfig(duration, interval), booleanProducer)
}

@ExperimentalKotest
suspend fun until(
   config: PatienceConfig = PatienceConfig.default,
   booleanProducer: ConcurrencyProducer<Boolean>
) {
   until(config, listener = { it }, f = booleanProducer)
}

@ExperimentalKotest
suspend fun <T> until(
   duration: Millis,
   interval: Interval = 25L.fixed(),
   listener: UntilListener<T> = UntilListener.default,
   f: ConcurrencyProducer<T>
): T = until(PatienceConfig(duration, interval), listener, f)

@ExperimentalKotest
suspend fun <T> until(
   config: PatienceConfig = PatienceConfig.default,
   listener: UntilListener<T> = UntilListener.default,
   f: ConcurrencyProducer<T>
): T {
   val start = Instant(timeInMillis())
   val end = Instant(start.timeInMillis + config.duration)
   var times = 0

   while (timeInMillis() < end.timeInMillis) {
      val result = f()
      if (listener.onEval(result)) {
         return result
      }
      times++
      delay(config.interval.next(times))
   }

   val runtime = timeInMillis() - start.timeInMillis
   val message = "Until block failed after ${runtime}ms; attempted $times time(s); ${config.interval} delay between attempts"
   throw failure(message)
}
