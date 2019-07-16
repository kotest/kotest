package io.kotlintest.until

import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.math.pow

class ExponentialDelay(private val base: Int, private val unit: ChronoUnit) : Delay {
  override fun next(count: Int): Duration {
    val amount = base.toDouble().pow(count.toDouble()).toLong()
    return Duration.of(amount, unit)
  }
}

fun exponentialDelay(base: Int, unit: ChronoUnit) = ExponentialDelay(base, unit)