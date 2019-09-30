package io.kotest.until

import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.math.pow

class ExponentialInterval(private val base: Int, private val unit: ChronoUnit) : Interval {
  override fun next(count: Int): Duration {
    val amount = base.toDouble().pow(count.toDouble()).toLong()
    return Duration.of(amount, unit)
  }
}

fun exponentialInterval(base: Int, unit: ChronoUnit) = ExponentialInterval(base, unit)