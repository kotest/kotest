package io.kotest.framework.concurrency

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.common.ExperimentalKotest
import io.kotest.mpp.timeInMillis
import kotlin.reflect.KClass

@ExperimentalKotest
data class EventuallyConfig(
   val duration: Millis = Long.MAX_VALUE,
   val interval: Interval = 25L.fixed(),
   val retries: Int = Int.MAX_VALUE,
   val exceptions: Set<KClass<out Throwable>> = setOf(),
) {
   init {
      require(retries > 0) { "Retries should not be less than one" }
      require(duration > 0L) { "Duration cannot be negative" }
   }
}

@ExperimentalKotest
data class EventuallyState<T>(
   val result: T?,
   val start: Instant,
   val end: Instant,
   val iteration: Int,
   val firstError: Throwable?,
   val thisError: Throwable?,
)

@ExperimentalKotest
fun interface EventuallyListener<T> {
   fun onEval(state: EventuallyState<T>): Boolean

   companion object {
      val default = EventuallyListener<Any?> { it.thisError == null }
   }
}

@ExperimentalKotest
suspend fun <T> eventually(config: EventuallyConfig = EventuallyConfig(), listener: EventuallyListener<T> = EventuallyListener { it.thisError == null }, f: ConcurrencyProducer<T>): T {
   val start = Instant(timeInMillis())
   val end = Instant(timeInMillis() + config.duration)
   var times = 0
   var firstError: Throwable? = null
   var lastError: Throwable? = null
   var predicateFailedTimes = 0
   val originalAssertionMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

}
