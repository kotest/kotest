package io.kotest.property

import io.kotest.property.shrinker.DoubleShrinker
import io.kotest.property.shrinker.IntShrinker
import io.kotest.property.shrinker.LongShrinker
import kotlin.random.Random
import kotlin.random.nextLong

fun Arbitrary.Companion.int(
   iterations: Int,
   range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE,
   distribution: IntDistribution = IntDistribution.Uniform
) = object : Arbitrary<Int> {
   override fun edgecases(): Iterable<Int> = listOf(Int.MIN_VALUE, Int.MAX_VALUE, 0)
   override fun samples(random: Random): Sequence<PropertyInput<Int>> {
      return sequence {
         for (k in 0 until iterations) {
            val block = distribution.get(k, iterations, range.first.toLong()..range.last.toLong())
            val next = random.nextLong(block).toInt()
            val input = PropertyInput(next, IntShrinker)
            yield(input)
         }
      }
   }
}

sealed class IntDistribution {

   abstract fun get(k: Int, iterations: Int, range: LongRange): LongRange

   /**
    * Splits the range into discrete "blocks" to ensure that random values are distributed
    * across the entire range in a uniform manner.
    */
   object Uniform : IntDistribution() {
      override fun get(k: Int, iterations: Int, range: LongRange): LongRange {
         val step = (range.last - range.first) / iterations
         return (step * k)..(step * (k + 1))
      }
   }

   /**
    * Values are distributed according to the Pareto distribution.
    * See https://en.wikipedia.org/wiki/Pareto_distribution
    * Sometimes referred to as the 80-20 rule
    *
    * tl;dr - more values are produced at the lower bound than the upper bound.
    */
   object Pareto : IntDistribution() {
      override fun get(k: Int, iterations: Int, range: LongRange): LongRange {
         // this isn't really the pareto distribution so either implement it properly, or rename this implementation
         val step = (range.last - range.first) / iterations
         return 0..(step * k + 1)
      }
   }
}

fun Arbitrary.Companion.long(
   iterations: Int,
   range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE
) = object : Arbitrary<Long> {
   override fun edgecases(): Iterable<Long> = listOf(Long.MIN_VALUE, Long.MAX_VALUE, 0)
   override fun samples(random: Random): Sequence<PropertyInput<Long>> {
      return sequence {
         for (k in 0 until iterations) {
            val next = random.nextLong(range)
            val input = PropertyInput(next, LongShrinker)
            yield(input)
         }
      }
   }
}

/**
 * Returns a stream of values where each value is a randomly
 * chosen Double.
 */
fun Arbitrary.Companion.double(): Arbitrary<Double> = object : Arbitrary<Double> {

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
      }
   }
}

fun Arbitrary.Companion.positiveDoubles(): Arbitrary<Double> = double().filter { it > 0.0 }
fun Arbitrary.Companion.negativeDoubles(): Arbitrary<Double> = double().filter { it < 0.0 }

/**
 * Returns an [Arbitrary] which is the same as [Arbitrary.double] but does not include +INFINITY, -INFINITY or NaN.
 *
 * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
 */
fun Arbitrary.Companion.numericDoubles(
   from: Double = Double.MIN_VALUE,
   to: Double = Double.MAX_VALUE
): Arbitrary<Double> = object : Arbitrary<Double> {
   val literals = listOf(0.0, 1.0, -1.0, 1e300, Double.MIN_VALUE, Double.MAX_VALUE).filter { it in (from..to) }
   override fun edgecases(): Iterable<Double> = literals
   override fun samples(random: Random): Sequence<PropertyInput<Double>> {
      return generateSequence {
         val d = random.nextDouble()
         PropertyInput(d, DoubleShrinker)
      }
   }
}
