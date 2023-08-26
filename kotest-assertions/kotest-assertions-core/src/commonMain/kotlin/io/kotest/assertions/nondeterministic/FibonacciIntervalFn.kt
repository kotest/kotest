package io.kotest.assertions.nondeterministic

import io.kotest.common.KotestInternal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.milliseconds

/**
 * Fibonacci delay implements a delay where each duration is calculated as a multiplier
 * of the fibonacci sequence, 0, 1, 1, 2, 3, 5....
 *
 * Some people start fib at 0, some at 1.
 * This implementation starts with 0 as per https://en.wikipedia.org/wiki/Fibonacci_number
 *
 * @param offset   Added to the count, so if the offset is 4, then the first value will be the 4th fib number.
 * @param base The duration that is multiplied by the fibonacci value
 * @param max the maximum duration to clamp the resulting duration to defaults to [FibonacciIntervalFn.defaultMax]
 */
@KotestInternal
class FibonacciIntervalFn(private val base: Duration, private val offset: Int, private val max: Duration?) : DurationFn {

   init {
      require(offset >= 0) { "Offset must be greater than or equal to 0" }
   }

   override fun toString() = "FibonacciInterval(${::base.name}=$base, ${::offset.name}=$offset, ${::max.name}=${max?.toString()})"

   override fun next(count: Int): Duration {
      val baseMs = base.inWholeMilliseconds
      val total = baseMs * fibonacci(offset + count)
      val result = total.milliseconds
      return if (max == null) result else minOf(max, result)
   }

   companion object {
      val defaultMax = 1.minutes
   }
}

fun Duration.fibonacci(max: Duration = FibonacciIntervalFn.defaultMax) = FibonacciIntervalFn(this, 0, max)

internal fun fibonacci(n: Int): Int {
   tailrec fun fib(k: Int, current: Int, previous: Int): Int = when (k) {
      0 -> previous
      1 -> current
      else -> fib(k - 1, current + previous, current)
   }
   return fib(n, 1, 0)
}
