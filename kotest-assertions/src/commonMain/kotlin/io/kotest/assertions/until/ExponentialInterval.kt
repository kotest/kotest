package io.kotest.assertions.until

import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class ExponentialInterval(private val base: Duration) : Interval {
   override fun next(count: Int): Duration {
      val amount = base.inMilliseconds.pow(count.toDouble()).toLong()
      return amount.milliseconds
   }
}

@UseExperimental(ExperimentalTime::class)
fun Duration.exponential() = ExponentialInterval(this)
