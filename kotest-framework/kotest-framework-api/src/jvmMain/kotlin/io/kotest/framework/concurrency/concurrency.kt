package io.kotest.framework.concurrency

import io.kotest.common.ExperimentalKotest

typealias Millis = Long // TODO: inline class this in 4.6.x (when we have kotlin 1.5)

typealias ConcurrencyProducer<T> = suspend () -> T

/**
 * Will be replaced with [kotlin.time.Duration] when that API is no longer experimental
 */
@ExperimentalKotest
data class Instant(val timeInMillis: Millis)

@ExperimentalKotest
data class PatienceConfig constructor(val duration: Millis, val interval: Interval) {
   companion object {
      val default = PatienceConfig(Long.MAX_VALUE, 25L.fixed())
   }
}
