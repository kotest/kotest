package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.math.round

private val finiteDoubleEdgecases = listOf(
   0.0,
   -0.0,
   1.0,
   -1.0,
   1e300,
   -1e300,
   Double.MIN_VALUE,
   -Double.MIN_VALUE,
   Double.MAX_VALUE,
   -Double.MAX_VALUE
)

private val nonFiniteDoubleEdgecases = listOf(
   Double.POSITIVE_INFINITY,
   Double.NEGATIVE_INFINITY,
   Double.NaN
)

private val doubleEdgecases = finiteDoubleEdgecases + nonFiniteDoubleEdgecases

/**
 * Returns an [Arb] where each value is a randomly chosen Double.
 */
fun Arb.Companion.double(): Arb<Double> {
   return arb(DoubleShrinker, doubleEdgecases) { it.random.nextDouble(-Double.MAX_VALUE, Double.MAX_VALUE) }
}

/**
 * Returns an [Arb] which is the same as [double] but does not include +INFINITY, -INFINITY or NaN.
 *
 * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
 */
fun Arb.Companion.numericDoubles(
   from: Double = -Double.MAX_VALUE,
   to: Double = Double.MAX_VALUE
): Arb<Double> {
   val edgecases = (finiteDoubleEdgecases + listOf(from, to)).filter { it in (from..to) }.distinct()
   return arb(DoubleShrinker, edgecases) { it.random.nextDouble(from, to) }
}

fun Arb.Companion.positiveDoubles(): Arb<Double> = double().filter { it > 0.0 }
fun Arb.Companion.negativeDoubles(): Arb<Double> = double().filter { it < 0.0 }

object DoubleShrinker : Shrinker<Double> {
   override fun shrink(value: Double): List<Double> {
      return if (value == 0.0) emptyList() else {
         val a = listOf(0.0, 1.0, -1.0, abs(value), value / 3, value / 2)
         val b = (1..5).map { value - it }.reversed().filter { it > 0 }
         (a + b + round(value)).distinct()
      }
   }
}
