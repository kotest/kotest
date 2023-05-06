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
 * The edge cases are [Float.NEGATIVE_INFINITY], [min], -1.0, -[Float.MIN_VALUE], -0.0, 0.0, [Float.MIN_VALUE], 1.0,
 * [max], [Float.POSITIVE_INFINITY] and [Float.NaN] which are only included if they are in the provided range.
 *
 * @see numericFloat to only produce numeric [Float]s
 */
fun Arb.Companion.float(min: Float = -Float.MAX_VALUE, max: Float = Float.MAX_VALUE): Arb<Float> = float(min..max)

/**
 * Returns an [Arb] that produces [Float]s in [range].
 * The edge cases are [Float.NEGATIVE_INFINITY], [ClosedFloatingPointRange.start], -1.0, -[Float.MIN_VALUE], -0.0,
 * 0.0, [Float.MIN_VALUE], 1.0, [ClosedFloatingPointRange.endInclusive], [Float.POSITIVE_INFINITY] and [Float.NaN] which
 * are only included if they are in the provided range.
 */
fun Arb.Companion.float(range: ClosedFloatingPointRange<Float> = -Float.MAX_VALUE..Float.MAX_VALUE): Arb<Float> =
   arbitrary(
      (numericEdgeCases.filter { it in range } +
         listOf(-1.0F, -0.0F, 0.0F, 1.0F).filter { it in range }.map { it / 0.0F } +
         listOf(range.start, range.endInclusive)
         ).distinct(),
      FloatShrinker
   ) {
      val sign = it.random.nextBits(1)
      val exponent = it.random.nextInt(0,254)
      val mantissa = it.random.nextBits(23)
      val bits = (sign shl 31) or (exponent shl 23) or mantissa
      Float.fromBits(bits)
   }

/**
 * Returns an [Arb] that produces positive [Float]s from [Float.MIN_VALUE] to [max] (inclusive).
 * The edge cases are [Float.MIN_VALUE], 1.0, [max] and [Float.POSITIVE_INFINITY] which are only included if they are
 * in the provided range.
 */
fun Arb.Companion.positiveFloat(): Arb<Float> = float(Float.MIN_VALUE, Float.MAX_VALUE)

/**
 * Returns an [Arb] that produces negative [Float]s from [min] to -[Float.MIN_VALUE] (inclusive).
 * The edge cases are [Float.NEGATIVE_INFINITY], [min], -1.0 and -[Float.MIN_VALUE] which are only included if they
 * are in the provided range.
 */
fun Arb.Companion.negativeFloat(): Arb<Float> = float(-Float.MAX_VALUE, -Float.MIN_VALUE)

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
   if (min == -Float.MAX_VALUE && max == Float.MAX_VALUE) {
      val sign = it.random.nextBits(1)
      val exponent = it.random.nextInt(0,254)
      val mantissa = it.random.nextBits(23)
      val bits = (sign shl 31) or (exponent shl 23) or mantissa
      Float.fromBits(bits)
   }else {
      it.random.nextDouble(min.toDouble(), max.toDouble()).toFloat()
   }
}

@Deprecated("use numericFloat", ReplaceWith("numericFloat(from, to)"))
fun Arb.Companion.numericFloats(from: Float = -Float.MAX_VALUE, to: Float = Float.MAX_VALUE): Arb<Float> =
   numericFloat(from, to)

/**
 * Returns an [Arb] that produces [FloatArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
fun Arb.Companion.floatArray(length: Gen<Int>, content: Arb<Float>): Arb<FloatArray> =
   toPrimitiveArray(length, content, Collection<Float>::toFloatArray)
