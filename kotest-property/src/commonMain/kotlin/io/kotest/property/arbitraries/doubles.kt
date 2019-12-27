package io.kotest.property.arbitraries

import io.kotest.property.Arbitrary
import io.kotest.property.PropertyInput
import io.kotest.property.filter
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random

/**
 * Returns a stream of values where each value is a randomly
 * chosen Double.
 */

fun Arbitrary.Companion.double(iterations: Int): Arbitrary<Double> = object : Arbitrary<Double> {

   val literals = listOf(
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

   override fun edgecases(): Iterable<Double> = literals

   override fun samples(random: Random): Sequence<PropertyInput<Double>> {
      return generateSequence {
         val d = random.nextDouble()
         PropertyInput(d, DoubleShrinker)
      }.take(iterations)
   }
}

/**
 * Returns an [Arbitrary] which is the same as [double] but does not include +INFINITY, -INFINITY or NaN.
 *
 * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
 */
fun Arbitrary.Companion.numericDoubles(
   iterations: Int,
   from: Double = Double.MIN_VALUE,
   to: Double = Double.MAX_VALUE
): Arbitrary<Double> = object : Arbitrary<Double> {
   val literals = listOf(0.0, 1.0, -1.0, 1e300, Double.MIN_VALUE, Double.MAX_VALUE).filter { it in (from..to) }
   override fun edgecases(): Iterable<Double> = literals
   override fun samples(random: Random): Sequence<PropertyInput<Double>> {
      return generateSequence {
         val d = random.nextDouble()
         PropertyInput(d, DoubleShrinker)
      }.take(iterations)
   }
}

fun Arbitrary.Companion.positiveDoubles(iterations: Int): Arbitrary<Double> = double(iterations).filter { it > 0.0 }
fun Arbitrary.Companion.negativeDoubles(iterations: Int): Arbitrary<Double> = double(iterations).filter { it < 0.0 }

object DoubleShrinker : Shrinker<Double> {
   override fun shrink(value: Double): List<PropertyInput<Double>> {
      return if (value == 0.0) emptyList() else {
         val a = listOf(0.0, 1.0, -1.0, abs(value), value / 3, value / 2)
         val b = (1..5).map { value - it }.reversed().filter { it > 0 }
         (a + b + round(value)).distinct().map { PropertyInput(it, this) }
      }
   }
}
