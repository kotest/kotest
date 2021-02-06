package io.kotest.assertions.until

import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.milliseconds

class ExponentialInterval(private val base: Duration, private val cap: Duration?) : Interval {
   override fun toString() = "ExponentialInterval(${::base.name}=$base, ${::cap.name}=$cap)"

   override fun next(count: Int): Duration {
      val amount = base.inMilliseconds.pow(count.toDouble()).toLong()
      return amount.milliseconds
   }
}

fun Duration.exponential(cap: Duration? = null) = ExponentialInterval(this, cap)
