package io.kotest.framework.concurrency

import io.kotest.common.ExperimentalKotest

typealias Millis = Long // TODO: inline class this in 4.5.x

typealias ConcurrencyProducer<T> = suspend () -> T

@ExperimentalKotest
data class Instant(val timeInMillis: Millis)

@ExperimentalKotest
data class PatienceConfig constructor(val duration: Millis, val interval: Interval) {
   companion object {
      val default = PatienceConfig(Long.MAX_VALUE, 25L.fixed())
   }
}
