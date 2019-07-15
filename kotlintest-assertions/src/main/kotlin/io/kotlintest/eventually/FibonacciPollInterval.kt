package io.kotlintest.eventually

import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * Create an instance of the [FibonacciPollInterval] with a supplied time unit.
 *
 * @param offset   The fibonacci offset. For example if offset is 5 and poll count is 1 then the returned duration will be 8 (since  `fib(6)` is equal to 8).
 * @param unit The time unit
 */
open class FibonacciPollInterval(private val offset: Int, private val unit: ChronoUnit) : PollInterval {

  init {
    if (offset <= -1) {
      throw IllegalArgumentException("Offset must be greater than or equal to -1")
    }
  }

  override fun next(count: Int): Duration {
    return Duration.of(fibonacci(offset + count).toLong(), unit)
  }

  private fun fibonacci(value: Int): Int = fib(value, 1, 0)

  private tailrec fun fib(value: Int, current: Int, previous: Int): Int {
    if (value == 0) {
      return previous
    } else if (value == 1) {
      return current
    }
    return fib(value - 1, current + previous, current)
  }
}