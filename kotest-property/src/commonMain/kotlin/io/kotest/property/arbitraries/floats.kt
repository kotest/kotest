package io.kotest.property.arbitraries

import io.kotest.property.Arbitrary
import io.kotest.property.PropertyInput
import io.kotest.property.shrinker.Shrinker
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random

/**
 * Returns an [Arbitrary] where each value is a Float.
 *
 * The edge cases are:
 *
 * [Float.MIN_VALUE],
 * [Float.MAX_VALUE],
 * [Float.NEGATIVE_INFINITY],
 * [Float.NaN],
 * [Float.POSITIVE_INFINITY]
 *
 */
fun Arbitrary.Companion.float(iterations: Int): Arbitrary<Float> = object : Arbitrary<Float> {

   val literals = listOf(
      0F,
      Float.MIN_VALUE,
      Float.MAX_VALUE,
      Float.NEGATIVE_INFINITY,
      Float.NaN,
      Float.POSITIVE_INFINITY
   )

   override fun edgecases(): Iterable<Float> = literals
   override fun samples(random: Random): Sequence<PropertyInput<Float>> {
      return generateSequence { PropertyInput(random.nextFloat(), FloatShrinker) }
         .take(iterations)
   }
}

object FloatShrinker : Shrinker<Float> {
   override fun shrink(value: Float): List<PropertyInput<Float>> {
      return if (value == 0F) emptyList() else {
         val a = listOf(0F, 1F, -1F, abs(value), value / 3, value / 2)
         val b = (1..5).map { value - it }.reversed().filter { it > 0 }
         (a + b + round(value))
            .distinct()
            .map { PropertyInput(it, FloatShrinker) }
      }
   }
}
