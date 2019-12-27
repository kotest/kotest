package io.kotest.property.arbitrary

import io.kotest.property.Arbitrary
import io.kotest.property.PropertyInput
import io.kotest.property.Shrinker
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

/**
 * Returns an [Arbitrary] which is the same as [double] but does not include +INFINITY, -INFINITY or NaN.
 *
 * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
 */
fun Arbitrary.Companion.numericFloats(
   from: Float = Float.MIN_VALUE,
   to: Float = Float.MAX_VALUE
): Arbitrary<Float> = object : Arbitrary<Float> {

   val literals = listOf(0.0F, 1.0F, -1.0F, Float.MIN_VALUE, Float.MAX_VALUE).filter { it in (from..to) }

   override fun edgecases(): Iterable<Float> = literals

   // There's no nextFloat(from, to) method, so borrowing it from Double
   override fun samples(random: Random): Sequence<PropertyInput<Float>> {
      return generateSequence {
         PropertyInput(random.nextDouble(from.toDouble(), to.toDouble()).toFloat(), FloatShrinker)
      }
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
