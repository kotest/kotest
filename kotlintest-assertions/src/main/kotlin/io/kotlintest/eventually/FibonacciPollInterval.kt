package io.kotlintest.eventually

import java.time.Duration

/**
 * Create an instance of the [FibonacciPollInterval] with a supplied time unit.
 *
 * @param offset   The fibonacci offset. Eg if offset is 5 and poll count is 1 then the duration will be base * fib(6).
 * @param base The duration that is multiplied by the fibonacci value
 */
class FibonacciPollInterval(private val base: Duration, private val offset: Int) : PollInterval {

  init {
    if (offset <= -1) {
      throw IllegalArgumentException("Offset must be greater than or equal to -1")
    }
  }

  override fun next(count: Int): Duration = base.multipliedBy(fibonacci(offset + count).toLong())

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

fun fibonacciInterval(base: Duration) = FibonacciPollInterval(base, 0)
fun fibonacciInterval(offset: Int, base: Duration) = FibonacciPollInterval(base, offset)