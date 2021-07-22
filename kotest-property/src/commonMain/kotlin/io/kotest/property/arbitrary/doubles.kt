package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.math.round

private val numericEdgeCases = listOf(-1.0, -Double.MIN_VALUE, -0.0, 0.0, Double.MIN_VALUE, 1.0)

private val nonFiniteEdgeCases = listOf(Double.NEGATIVE_INFINITY, Double.NaN, Double.POSITIVE_INFINITY)

object DoubleShrinker : Shrinker<Double> {
   override fun shrink(value: Double): List<Double> {
      if (value == 0.0 || !value.isFinite())
         return emptyList()

      val bits = value.toBits()

      return if (bits and 0x000F_FFFF_FFFF_FFFFL == 0L)
         emptyList()
      else
         listOf(Double.fromBits(bits and (bits - 1)))
   }
}

/**
 * Returns an [Arb] that produces [Double]s from [min] to [max] (inclusive).
 * The edge cases are [Double.NEGATIVE_INFINITY], [min], -1.0, -[Double.MIN_VALUE], -0.0, 0.0, [Double.MIN_VALUE], 1.0,
 * [max], [Double.POSITIVE_INFINITY] and [Double.NaN] which are only included if they are in the provided range.
 *
 * @see numericDouble to only produce numeric [Double]s
 */
fun Arb.Companion.double(min: Double = -Double.MAX_VALUE, max: Double = Double.MAX_VALUE): Arb<Double> =
   double(min..max)

/**
 * Returns an [Arb] that produces [Double]s in [range].
 * The edge cases are [Double.NEGATIVE_INFINITY], [ClosedFloatingPointRange.start], -1.0, -[Double.MIN_VALUE], -0.0,
 * 0.0, [Double.MIN_VALUE], 1.0, [ClosedFloatingPointRange.endInclusive], [Double.POSITIVE_INFINITY] and [Double.NaN]
 * which are only included if they are in the provided range.
 */
fun Arb.Companion.double(
   range: ClosedFloatingPointRange<Double> = -Double.MAX_VALUE..Double.MAX_VALUE
): Arb<Double> = arbitrary(
   (numericEdgeCases.filter { it in range } + nonFiniteEdgeCases  + listOf(range.start, range.endInclusive)).distinct(),
   DoubleShrinker
) {
   it.random.nextDouble(range.start, range.endInclusive)
}

/**
 * Returns an [Arb] that produces positive [Double]s from [Double.MIN_VALUE] to [max] (inclusive).
 * The edge cases are [Double.MIN_VALUE], 1.0, [max] and [Double.POSITIVE_INFINITY] which are only included if they are
 * in the provided range.
 */
fun Arb.Companion.positiveDouble(max: Double = Double.MAX_VALUE): Arb<Double> = double(Double.MIN_VALUE, max)

@Deprecated("use positiveDouble", ReplaceWith("positiveDouble()"))
fun Arb.Companion.positiveDoubles(): Arb<Double> = positiveDouble()

/**
 * Returns an [Arb] that produces negative [Double]s from [min] to -[Double.MIN_VALUE] (inclusive).
 * The edge cases are [Double.NEGATIVE_INFINITY], [min], -1.0 and -[Double.MIN_VALUE] which are only included if they
 * are in the provided range.
 */
fun Arb.Companion.negativeDouble(min: Double = -Double.MAX_VALUE): Arb<Double> = double(min, -Double.MIN_VALUE)

@Deprecated("use negativeDouble", ReplaceWith("negativeDouble()"))
fun Arb.Companion.negativeDoubles(): Arb<Double> = negativeDouble()

/**
 * Returns an [Arb] that produces numeric [Double]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1.0, -[Double.MIN_VALUE], -0.0, 0.0, [Double.MIN_VALUE], 1.0 and [max] which are only
 * included if they are in the provided range.
 *
 * @see double to also have non numeric [Double]s as edge cases.
 */
fun Arb.Companion.numericDouble(
   min: Double = -Double.MAX_VALUE,
   max: Double = Double.MAX_VALUE
): Arb<Double> = arbitrary(
   (numericEdgeCases.filter { it in (min..max) } + listOf(min, max)).distinct(), DoubleShrinker
) { it.random.nextDouble(min, max) }

@Deprecated("use numericDouble", ReplaceWith("numericDouble(from, to)"))
fun Arb.Companion.numericDoubles(from: Double = -Double.MAX_VALUE, to: Double = Double.MAX_VALUE): Arb<Double> =
   numericDouble(from, to)

/**
 * Returns an [Arb] that produces [DoubleArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
fun Arb.Companion.doubleArray(length: Gen<Int>, content: Arb<Double>): Arb<DoubleArray> =
   toPrimitiveArray(length, content, Collection<Double>::toDoubleArray)
