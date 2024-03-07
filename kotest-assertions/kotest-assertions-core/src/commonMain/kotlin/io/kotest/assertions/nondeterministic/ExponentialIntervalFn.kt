package io.kotest.assertions.nondeterministic

import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

/**
 * Exponential interval implements a delay where each duration is calculated as a multiplier
 * of an exponent of the default [ExponentialIntervalFn.defaultFactor] or a user specified factor.
 *
 * You should start at 0 to get the base value back and at 1 for the second value in the series, e.g.:
 * val interval = 2.seconds.exponential(max = Duration.INFINITE) which will produce 2s, 4s, 8s, etc.
 *
 * @param base the duration that is multiplied by the exponentiated factor
 * @param factor the factor to exponentiate by the current iteration value
 * @param max the maximum duration to clamp the resulting duration to defaults to [ExponentialIntervalFn.defaultMax]
 */
class ExponentialIntervalFn(private val base: Duration, private val factor: Double, private val max: Duration?) : DurationFn {
   override fun toString() = "ExponentialInterval(${::base.name}=$base, ${::factor.name}=$factor, ${::max.name}=$max)"

   override fun next(count: Int): Duration {
      val result = base * factor.pow(count)
      return if (max == null) result else minOf(max, result)
   }

   companion object {
      val defaultMax = 2.hours
      const val defaultFactor = 2.0
   }
}

fun Duration.exponential(factor: Double = ExponentialIntervalFn.defaultFactor, max: Duration? = ExponentialIntervalFn.defaultMax) =
   ExponentialIntervalFn(this, factor, max)
