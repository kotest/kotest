package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.Shrinker
import kotlin.math.absoluteValue

private val numericEdgeCases = listOf(-1.0F, -Float.MIN_VALUE, -0.0F, 0.0F, Float.MIN_VALUE, 1.0F)

private val nonFiniteEdgeCases = listOf(Float.NEGATIVE_INFINITY, Float.NaN, Float.POSITIVE_INFINITY)

object FloatShrinker : Shrinker<Float> {
   override fun shrink(value: Float): List<Float> =
      if (value == 0f || !value.isFinite() || value.absoluteValue < 10 * Float.MIN_VALUE)
         emptyList()
      else
         listOfNotNull(DoubleShrinker.shrink(value.toString())?.toFloat())
}

/**
 * Returns an [Arb] that produces [Float]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1.0, -[Float.MIN_VALUE], -0.0, 0.0, [Float.MIN_VALUE], 1.0 and [max]
 * which are only included if they are in the provided range.
 * The non-finite edge cases are [Float.NEGATIVE_INFINITY], [Float.POSITIVE_INFINITY] and [Float.NaN]
 * which are only included if they are in the provided range and includeNonFiniteEdgeCases flag is true.
 *
 * @see numericFloat to only produce numeric [Float]s
 */
fun Arb.Companion.float(min: Float = -Float.MAX_VALUE, max: Float = Float.MAX_VALUE, includeNonFiniteEdgeCases: Boolean = true): Arb<Float> = float(min..max, includeNonFiniteEdgeCases)

/**
 * Returns an [Arb] that produces [Float]s in [range].
 * The numeric edge cases are [ClosedFloatingPointRange.start], -1.0, -[Float.MIN_VALUE], -0.0,
 * 0.0, [Float.MIN_VALUE], 1.0 and [ClosedFloatingPointRange.endInclusive]
 * which are only included if they are in the provided range.
 * The non-finite edge cases are [Float.NEGATIVE_INFINITY], [Float.POSITIVE_INFINITY] and [Float.NaN]
 * which are only included if they are in the provided range and includeNonFiniteEdgeCases flag is true.
 */
fun Arb.Companion.float(range: ClosedFloatingPointRange<Float> = -Float.MAX_VALUE..Float.MAX_VALUE, includeNonFiniteEdgeCases: Boolean = true): Arb<Float> =
   arbitrary(
      (numericEdgeCases.filter { it in range } + listOf(range.start, range.endInclusive)).distinct() + getNonFiniteEdgeCases(range, includeNonFiniteEdgeCases),
      FloatShrinker
   ) {
      it.random.nextDouble(range.start.toDouble(), range.endInclusive.toDouble()).toFloat()
   }

/**
 * Returns an [Arb] that produces positive [Float]s from [Float.MIN_VALUE] to [max] (inclusive).
 * The numeric edge cases are [Float.MIN_VALUE], 1.0, and [max] which are only included if they are in the provided range.
 * The non-finite edge case is [Float.POSITIVE_INFINITY]
 * which is only included if is in the provided range and includeNonFiniteEdgeCases flag is true.
 */
fun Arb.Companion.positiveFloat(includeNonFiniteEdgeCases: Boolean = true): Arb<Float> = float(Float.MIN_VALUE, Float.MAX_VALUE, includeNonFiniteEdgeCases)

/**
 * Returns an [Arb] that produces negative [Float]s from [min] to -[Float.MIN_VALUE] (inclusive).
 * The numeric edge cases are [min], -1.0 and -[Float.MIN_VALUE] which are only included if they are in the provided range.
 * The non-finite edge case is [Float.NEGATIVE_INFINITY]
 * which is only included if it is in the provided range and includeNonFiniteEdgeCases flag is true.
 */
fun Arb.Companion.negativeFloat(includeNonFiniteEdgeCases: Boolean = true): Arb<Float> = float(-Float.MAX_VALUE, -Float.MIN_VALUE, includeNonFiniteEdgeCases)

/**
 * Returns an [Arb] that produces numeric [Float]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1.0, -[Float.MIN_VALUE], -0.0, 0.0, [Float.MIN_VALUE], 1.0 and [max] which are only
 * included if they are in the provided range.
 *
 * @see float to also have non numeric [Float]s as edge cases.
 */
fun Arb.Companion.numericFloat(
   min: Float = -Float.MAX_VALUE,
   max: Float = Float.MAX_VALUE
): Arb<Float> = arbitrary((numericEdgeCases.filter { it in min..max } + listOf(min, max)).distinct(), FloatShrinker) {
   it.random.nextDouble(min.toDouble(), max.toDouble()).toFloat()
}

/**
 * Returns an [Arb] that produces [FloatArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
fun Arb.Companion.floatArray(length: Gen<Int>, content: Arb<Float>): Arb<FloatArray> =
   toPrimitiveArray(length, content, Collection<Float>::toFloatArray)

private fun getNonFiniteEdgeCases(range: ClosedFloatingPointRange<Float>, includeNonFiniteEdgeCases: Boolean) : List<Float> {
   return if (includeNonFiniteEdgeCases) {
      if (range == -Float.MAX_VALUE..Float.MAX_VALUE) nonFiniteEdgeCases
      else if (range.start == -Float.MAX_VALUE) nonFiniteEdgeCases.filter { it <= range.endInclusive }
      else if (range.endInclusive == Float.MAX_VALUE) nonFiniteEdgeCases.filter { it >= range.start }
      else emptyList()
   } else emptyList()
}
