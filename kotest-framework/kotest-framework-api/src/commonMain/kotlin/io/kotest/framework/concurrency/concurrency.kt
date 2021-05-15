package io.kotest.framework.concurrency

import io.kotest.common.ExperimentalKotest

/**
 * Will be replaced with [kotlin.time.Duration] when that API is no longer experimental
 */
typealias Millis = Long

typealias ConcurrencyProducer<T> = suspend () -> T
typealias ConcurrencyConsumer<T> = suspend (T) -> Boolean

@ExperimentalKotest
data class PatienceConfig constructor(val duration: Millis = defaultDuration, val interval: Interval = defaultInterval) {
   companion object {
      const val defaultDuration: Millis = 3_600_000L
      val defaultInterval = 25L.fixed()
      val default = PatienceConfig(defaultDuration, defaultInterval)
   }
}
