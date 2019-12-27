package io.kotest.property.arbitraries

import io.kotest.property.Arbitrary
import io.kotest.property.PropertyInput
import io.kotest.property.filter
import io.kotest.property.Shrinker
import io.kotest.property.setEdgeCases
import kotlin.math.abs
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

/**
 * Returns an [Arbitrary] where each value is a randomly chosen positive integer.
 * The edge cases are: [Int.MAX_VALUE]
 */
fun Arbitrary.Companion.positiveIntegers(iterations: Int): Arbitrary<Int> =
   int(iterations).setEdgeCases(Int.MAX_VALUE).filter { it > 0 }


/**
 * Returns an [Arbitrary] where each value is a randomly chosen negative integer.
 * The edge cases are: [Int.MIN_VALUE]
 */
fun Arbitrary.Companion.negativeIntegers(iterations: Int): Arbitrary<Int> =
   int(iterations).setEdgeCases(Int.MIN_VALUE).filter { it > 0 }

/**
 * Returns an [Arbitrary] where each value is a randomly chosen natural integer.
 * The edge cases are: [Int.MAX_VALUE]
 */
fun Arbitrary.Companion.nats(iterations: Int): Arbitrary<Int> = int(iterations).filter { it >= 0 }


object IntShrinker : Shrinker<Int> {
   override fun shrink(value: Int): List<PropertyInput<Int>> =
      when (value) {
         0 -> emptyList()
         1, -1 -> listOf(PropertyInput(0))
         else -> {
            val a = listOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct()
               .filterNot { it == value }
               .map { PropertyInput(it, this) }
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
