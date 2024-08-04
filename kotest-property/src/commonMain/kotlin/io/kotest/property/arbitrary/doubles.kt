package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.Shrinker
import kotlin.math.absoluteValue

private val numericEdgeCases = listOf(-1.0, -Double.MIN_VALUE, -0.0, 0.0, Double.MIN_VALUE, 1.0)

private val nonFiniteEdgeCases = listOf(Double.NEGATIVE_INFINITY, Double.NaN, Double.POSITIVE_INFINITY)

object DoubleShrinker : Shrinker<Double> {
   private val pattern = Regex("""([+-]|)([0-9]*)(\.[0-9]*|)(e[+-]?[0-9]+|)""", RegexOption.IGNORE_CASE)

   override fun shrink(value: Double): List<Double> =
      if (value == 0.0 || !value.isFinite() || value.absoluteValue < 10 * Double.MIN_VALUE)
         emptyList()
      else
         listOfNotNull(shrink(value.toString())?.toDouble())

   fun shrink(value: String): String? {
      val matches = pattern.matchEntire(value) ?: return null
      val parts = matches.groupValues.drop(1)
      val (signPart, intPart, fracPart_, expPart) = parts
      val fracPart = fracPart_.trimEnd { it == '0' }
      val numberPart = if (fracPart.isNotEmpty() && fracPart.last().isDigit()) {
         "$intPart${fracPart.dropLast(1)}"
      } else {
         val length = intPart.length
         val index = intPart.indexOfLast { it != '0' }.let { if (it == -1) length else it }

         if (index == 0) {
            return null
         }

         val head = intPart.take(index)
         val tail = intPart.takeLast(length - index - 1)

         "${head}0$tail"
      }

      return "$signPart$numberPart$expPart"
   }
}

/**
 * Returns an [Arb] that produces [Double]s from [min] to [max] (inclusive).
 * The numeric edge cases are [min], -1.0, -[Double.MIN_VALUE], -0.0, 0.0, [Double.MIN_VALUE], 1.0 and [max]
 * which are only included if they are in the provided range.
 * The non-finite edge cases are [Double.NEGATIVE_INFINITY], [Double.POSITIVE_INFINITY] and [Double.NaN]
 * which are only included if they are in the provided range and includeNonFiniteEdgeCases flag is true.
 *
 * @see numericDouble to only produce numeric [Double]s
 */
fun Arb.Companion.double(
   min: Double = -Double.MAX_VALUE,
   max: Double = Double.MAX_VALUE,
   includeNonFiniteEdgeCases: Boolean = true
): Arb<Double> = double(min..max, includeNonFiniteEdgeCases)

/**
 * Returns an [Arb] that produces [Double]s in [range].
 * The numeric edge cases are [ClosedFloatingPointRange.start], -1.0, -[Double.MIN_VALUE], -0.0,
 * 0.0, [Double.MIN_VALUE], 1.0 and [ClosedFloatingPointRange.endInclusive]
 * which are only included if they are in the provided range.
 * The non-finite edge cases are [Double.NEGATIVE_INFINITY], [Double.POSITIVE_INFINITY] and [Double.NaN]
 * which are only included if they are in the provided range and includeNonFiniteEdgeCases flag is true.
 */
fun Arb.Companion.double(
   range: ClosedFloatingPointRange<Double> = -Double.MAX_VALUE..Double.MAX_VALUE,
   includeNonFiniteEdgeCases: Boolean = true
): Arb<Double> = arbitrary(
   (numericEdgeCases.filter { it in range } + listOf(range.start, range.endInclusive)).distinct() + getNonFiniteEdgeCases(range, includeNonFiniteEdgeCases),
   DoubleShrinker
) {
   it.random.nextDouble(range.start, range.endInclusive)
}

/**
 * Returns an [Arb] that produces positive [Double]s from [Double.MIN_VALUE] to [max] (inclusive).
 * The numeric edge cases are [Double.MIN_VALUE], 1.0 and [max] which are only included if they are in the provided range.
 * The non-finite edge case is [Double.POSITIVE_INFINITY]
 * which is only included if is in the provided range and includeNonFiniteEdgeCases flag is true.
 */
fun Arb.Companion.positiveDouble(max: Double = Double.MAX_VALUE, includeNonFiniteEdgeCases: Boolean = true): Arb<Double> = double(Double.MIN_VALUE, max, includeNonFiniteEdgeCases)

/**
 * Returns an [Arb] that produces negative [Double]s from [min] to -[Double.MIN_VALUE] (inclusive).
 * The numeric edge cases are [min], -1.0 and -[Double.MIN_VALUE] which are only included if they are in the provided range.
 * The non-finite edge case is [Double.NEGATIVE_INFINITY]
 * which is only included if is in the provided range and includeNonFiniteEdgeCases flag is true.
 */
fun Arb.Companion.negativeDouble(min: Double = -Double.MAX_VALUE, includeNonFiniteEdgeCases: Boolean = true): Arb<Double> = double(min, -Double.MIN_VALUE, includeNonFiniteEdgeCases)

/**
 * Returns an [Arb] that produces numeric [Double]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1.0, -[Double.MIN_VALUE], -0.0, 0.0, [Double.MIN_VALUE], 1.0 and [max] which are only
 * included if they are in the provided range.
 *
 * @see double to also have non-numeric [Double]s as edge cases.
 */
fun Arb.Companion.numericDouble(
   min: Double = -Double.MAX_VALUE,
   max: Double = Double.MAX_VALUE
): Arb<Double> = arbitrary(
   (numericEdgeCases.filter { it in (min..max) } + listOf(min, max)).distinct(), DoubleShrinker
) { it.random.nextDouble(min, max) }

/**
 * Returns an [Arb] that produces [DoubleArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
fun Arb.Companion.doubleArray(length: Gen<Int>, content: Arb<Double>): Arb<DoubleArray> =
   toPrimitiveArray(length, content, Collection<Double>::toDoubleArray)

private fun getNonFiniteEdgeCases(range: ClosedFloatingPointRange<Double>, includeNonFiniteEdgeCases: Boolean) : List<Double> {
   return if (includeNonFiniteEdgeCases) {
      if (range == -Double.MAX_VALUE..Double.MAX_VALUE) nonFiniteEdgeCases
      else if (range.start == -Double.MAX_VALUE) nonFiniteEdgeCases.filter { it <= range.endInclusive }
      else if (range.endInclusive == Double.MAX_VALUE) nonFiniteEdgeCases.filter { it >= range.start }
      else emptyList()
   } else emptyList()
}
