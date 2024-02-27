package io.kotest.framework.concurrency

import kotlin.math.pow
import kotlin.time.Duration

private const val hour = 3_600_000L

/**
 * An [Interval] determines how often Kotest will invoke a predicate function for an [eventually], [until], or [continually] block.
 */
@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
interface Interval {

   /**
    * Returns the next delay in milliseconds.
    *
    * @param count The number of times the condition has been polled (evaluated) so far.
    * Always a positive integer.
    *
    * @return The duration of the next poll interval
    */
   fun next(count: Int): Long
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
class FixedInterval(private val duration: Long) : Interval {
   override fun toString() = "FixedInterval(${::duration.name}=$duration)"

   override fun next(count: Int): Long {
      return duration
   }
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
fun Long.fixed(): FixedInterval = FixedInterval(this)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
fun Duration.fixed() = this.inWholeMilliseconds.fixed()


/**
 * Fibonacci delay implements a delay where each duration is calculated as a multiplier
 * of the fibonacci sequence, 0, 1, 1, 2, 3, 5....
 *
 * Some people start fib at 0, some at 1.
 * This implementation starts with 0 as per https://en.wikipedia.org/wiki/Fibonacci_number
 *
 * @param offset Added to the count, so if the offset is 4, then the first value will be the 4th fib number.
 * @param base The duration that is multiplied by the fibonacci value
 * @param max the maximum duration to clamp the resulting duration to defaults to [FibonacciInterval.defaultMax]
 */
@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
class FibonacciInterval(private val base: Long, private val offset: Int, private val max: Long?) :
   Interval {
   init {
      require(offset >= 0) { "Offset must be greater than or equal to 0" }
   }

   override fun toString() =
      "FibonacciInterval(${::base.name}=$base, ${::offset.name}=$offset, ${::max.name}=${max?.toString() ?: "null"})"

   override fun next(count: Int): Long {
      val total = base * fibonacci(offset + count)
      return if (max == null) total else minOf(max, total)
   }

   companion object {
      const val defaultMax: Long = hour * 2
   }
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
fun Long.fibonacci(max: Long? = FibonacciInterval.defaultMax) = FibonacciInterval(this, 0, max)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
fun Duration.fibonacci(max: Duration? = null) =
   this.inWholeMilliseconds.fibonacci(max?.inWholeMilliseconds ?: FibonacciInterval.defaultMax)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
fun fibonacci(n: Int): Int {
   tailrec fun fib(k: Int, current: Int, previous: Int): Int = when (k) {
      0 -> previous
      1 -> current
      else -> fib(k - 1, current + previous, current)
   }
   return fib(n, 1, 0)
}

/**
 * Exponential interval implements a delay where each duration is calculated as a multiplier
 * of an exponent of the default [ExponentialInterval.defaultFactor] or a user specified factor.
 *
 * You should start at 0 to get the base value back and at 1 for the second value in the series, e.g.:
 * val interval = 2.seconds.exponential(max = Long.MAX_VALUE) which will produce 2s, 4s, 8s, etc.
 *
 * @param base the duration that is multiplied by the exponentiated factor
 * @param factor the factor to exponentiate by the current iteration value
 * @param max the maximum duration to clamp the resulting duration to defaults to [ExponentialInterval.defaultMax]
 */
@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
class ExponentialInterval(private val base: Long, private val factor: Double, private val max: Long?) :
   Interval {
   override fun toString() = "ExponentialInterval(${::base.name}=$base, ${::factor.name}=$factor, ${::max.name}=$max)"

   override fun next(count: Int): Long {
      val total = base * factor.pow(count)
      val duration = total.toLong()
      return if (max == null) duration else minOf(max, duration)
   }

   companion object {
      const val defaultMax: Long = hour * 2
      const val defaultFactor = 2.0
   }
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
fun Long.exponential(factor: Double = ExponentialInterval.defaultFactor, max: Long? = ExponentialInterval.defaultMax) =
   ExponentialInterval(this, factor, max)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
fun Duration.exponential(factor: Double = ExponentialInterval.defaultFactor, max: Duration? = null) =
   this.inWholeMilliseconds.exponential(factor, max?.inWholeMilliseconds ?: FibonacciInterval.defaultMax)


