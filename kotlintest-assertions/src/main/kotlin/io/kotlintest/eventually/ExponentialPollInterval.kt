package io.kotlintest.eventually

import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.math.pow

class ExponentialPollInterval(private val base: Int, private val unit: ChronoUnit) : PollInterval {
  override fun next(count: Int): Duration {
    val amount = base.toDouble().pow(count.toDouble()).toLong()
    return Duration.of(amount, unit)
  }
}