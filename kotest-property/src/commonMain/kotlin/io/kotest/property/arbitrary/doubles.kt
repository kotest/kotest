package io.kotest.property.arbitrary

import io.kotest.property.Shrinker
import io.kotest.property.Sample
import io.kotest.property.sampleOf
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random

/**
 * Returns an [Arb] where each value is a randomly chosen Double.
 */
fun Arb.Companion.doubles(): Arb<Double> = object : Arb<Double> {

   private val literals = listOf(
      0.0,
      1.0,
      -1.0,
      1e300,
      Double.MIN_VALUE,
      Double.MAX_VALUE,
      Double.NEGATIVE_INFINITY,
      Double.NaN,
      Double.POSITIVE_INFINITY
   )

   override fun edgecases(): List<Double> = literals
   override fun sample(random: Random): Sample<Double> = sampleOf(random.nextDouble(), DoubleShrinker)
}

/**
 * Returns an [Arb] which is the same as [doubles] but does not include +INFINITY, -INFINITY or NaN.
 *
 * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
 */
fun Arb.Companion.numericDoubles(
   from: Double = Double.MIN_VALUE,
   to: Double = Double.MAX_VALUE
): Arb<Double> = object : Arb<Double> {
   val literals = listOf(0.0, 1.0, -1.0, 1e300, Double.MIN_VALUE, Double.MAX_VALUE).filter { it in (from..to) }
   override fun edgecases(): List<Double> = literals
   override fun sample(random: Random): Sample<Double> = sampleOf(random.nextDouble(from, to), DoubleShrinker)
}

fun Arb.Companion.positiveDoubles(): Arb<Double> = doubles().filter { it > 0.0 }
fun Arb.Companion.negativeDoubles(): Arb<Double> = doubles().filter { it < 0.0 }

object DoubleShrinker : Shrinker<Double> {
   override fun shrink(value: Double): List<Double> {
      return if (value == 0.0) emptyList() else {
         val a = listOf(0.0, 1.0, -1.0, abs(value), value / 3, value / 2)
         val b = (1..5).map { value - it }.reversed().filter { it > 0 }
         (a + b + round(value)).distinct()
      }
   }
}
