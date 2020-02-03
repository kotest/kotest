package io.kotest.property.arbitrary

import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.math.round

/**
 * Returns an [Arb] where each value is a Float.
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
fun Arb.Companion.float(): Arb<Float> = arb(
   FloatShrinker,
   listOf(
      0F, 1.0F, -1.0F,
      Float.MIN_VALUE,
      Float.MAX_VALUE,
      Float.NEGATIVE_INFINITY,
      Float.NaN,
      Float.POSITIVE_INFINITY
   )
) { it.nextFloat() }

/**
 * Returns an [Arb] which is the same as [float] but does not include +INFINITY, -INFINITY or NaN.
 *
 * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
 */
fun Arb.Companion.numericFloats(
   from: Float = Float.MIN_VALUE,
   to: Float = Float.MAX_VALUE
) = arb(
   FloatShrinker,
   listOf(
      0F, 1.0F, -1.0F,
      Float.MIN_VALUE,
      Float.MAX_VALUE
   )
) { it.nextDouble(from.toDouble(), to.toDouble()).toFloat() }

object FloatShrinker : Shrinker<Float> {
   override fun shrink(value: Float): List<Float> {
      return if (value == 0F) emptyList() else {
         val a = listOf(0F, 1F, -1F, abs(value), value / 3, value / 2)
         val b = (1..5).map { value - it }.reversed().filter { it > 0 }
         (a + b + round(value))
            .distinct()
      }
   }
}
