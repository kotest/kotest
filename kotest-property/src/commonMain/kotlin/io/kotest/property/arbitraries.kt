package io.kotest.property

import io.kotest.properties.nextPrintableString
import io.kotest.property.shrinker.*
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

/**
 * Returns an [Arbitrary] where each value is a randomly chosen positive integer.
 * The edge cases are: [Int.MAX_VALUE]
 */
fun Arbitrary.Companion.positiveIntegers(iterations: Int): Arbitrary<Int> =
   int(iterations).withEdgeCases(Int.MAX_VALUE).filter { it > 0 }


/**
 * Returns an [Arbitrary] where each value is a randomly chosen negative integer.
 * The edge cases are: [Int.MIN_VALUE]
 */
fun Arbitrary.Companion.negativeIntegers(iterations: Int): Arbitrary<Int> =
   int(iterations).withEdgeCases(Int.MIN_VALUE).filter { it > 0 }

/**
 * Returns an [Arbitrary] where each value is a randomly chosen natural integer.
 * The edge cases are: [Int.MAX_VALUE]
 */
fun Arbitrary.Companion.nats(iterations: Int): Arbitrary<Int> = int(iterations).filter { it >= 0 }

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

fun <T> Arbitrary.Companion.constant(constant: T) = object : Arbitrary<T> {
   override fun edgecases(): Iterable<T> = emptyList()
   override fun samples(random: Random): Sequence<PropertyInput<T>> = sequenceOf(PropertyInput(constant))
}

/**
 * Returns a stream of values where each value is a randomly
 * chosen Double.
 */

fun Arbitrary.Companion.double(iterations: Int): Arbitrary<Double> = object : Arbitrary<Double> {

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
      }.take(iterations)
   }
}

fun Arbitrary.Companion.positiveDoubles(iterations: Int): Arbitrary<Double> = double(iterations).filter { it > 0.0 }
fun Arbitrary.Companion.negativeDoubles(iterations: Int): Arbitrary<Double> = double(iterations).filter { it < 0.0 }

/**
 * Returns an [Arbitrary] which is the same as [double] but does not include +INFINITY, -INFINITY or NaN.
 *
 * This will only generate numbers ranging from [from] (inclusive) to [to] (inclusive)
 */
fun Arbitrary.Companion.numericDoubles(
   iterations: Int,
   from: Double = Double.MIN_VALUE,
   to: Double = Double.MAX_VALUE
): Arbitrary<Double> = object : Arbitrary<Double> {
   val literals = listOf(0.0, 1.0, -1.0, 1e300, Double.MIN_VALUE, Double.MAX_VALUE).filter { it in (from..to) }
   override fun edgecases(): Iterable<Double> = literals
   override fun samples(random: Random): Sequence<PropertyInput<Double>> {
      return generateSequence {
         val d = random.nextDouble()
         PropertyInput(d, DoubleShrinker)
      }.take(iterations)
   }
}

/**
 * Returns an [Arbitrary] where each random value is a Byte.
 * The edge cases are [[Byte.MIN_VALUE], [Byte.MAX_VALUE], 0]
 */
fun Arbitrary.Companion.byte(iterations: Int) = int(iterations).map { it.ushr(Int.SIZE_BITS - Byte.SIZE_BITS).toByte() }

/**
 * Returns an [Arbitrary] where each random value is a String.
 * The edge cases values are:
 *
 * The empty string
 * A line separator
 * Multi-line string
 * a UTF8 string.
 */
fun Arbitrary.Companion.string(iterations: Int = 100, minSize: Int = 0, maxSize: Int = 100): Arbitrary<String> =
   object : Arbitrary<String> {

      val range = minSize..maxSize

      val literals = listOf(
         "",
         "\n",
         "\nabc\n123\n",
         "\u006c\u0069b/\u0062\u002f\u006d\u0069nd/m\u0061x\u002e\u0070h\u0070"
      )

      override fun edgecases(): Iterable<String> = literals.filter { it.length in range }

      override fun samples(random: Random): Sequence<PropertyInput<String>> {
         return generateSequence {
            random.nextPrintableString(range.first + random.nextInt(range.last - range.first + 1))
         }.map { PropertyInput(it, StringShrinker) }.take(iterations)
      }
   }

/**
 * Returns an [Arbitrary] where each generated value is a map, with the entries of the map
 * drawn from the given arbitrary. The size of each generated map is a random value between
 * the specified min and max bounds.
 *
 * There are no edgecases.
 *
 * This arbitrary uses a [Shrinker] which will reduce the size of a failing map by
 * removing elements until they map is empty.
 *
 * @see MapShrinker
 */
fun <K, V> Arbitrary.Companion.map(
   iterations: Int,
   arb: Arbitrary<Pair<K, V>>,
   minSize: Int = 1,
   maxSize: Int = 100
): Gen<Map<K, V>> = object : Arbitrary<Map<K, V>> {

   init {
      require(minSize >= 0) { "minSize must be positive" }
      require(maxSize >= 0) { "maxSize must be positive" }
   }

   override fun edgecases(): Iterable<Map<K, V>> = emptyList()

   override fun samples(random: Random): Sequence<PropertyInput<Map<K, V>>> {
      return generateSequence {
         val size = random.nextInt(minSize, maxSize)
         val map = arb.samples(random).take(size).map { it.value }.toList().toMap()
         PropertyInput(map, MapShrinker())
      }.take(iterations)
   }
}

/**
 * Returns an [Arbitrary] where each generated value is a map, with the entries of the map
 * drawn by combining values from the key arbitrary and value arbitrary. The size of each
 * generated map is a random value between the specified min and max bounds.
 *
 * There are no edgecases.
 *
 * This arbitrary uses a [Shrinker] which will reduce the size of a failing map by
 * removing elements until they map is empty.
 *
 * @see MapShrinker
 */
fun <K, V> Arbitrary.Companion.map(
   iterations: Int,
   keyArb: Arbitrary<K>,
   valueArb: Arbitrary<V>,
   minSize: Int = 1,
   maxSize: Int = 100
): Gen<Map<K, V>> = object : Arbitrary<Map<K, V>> {

   init {
      require(minSize >= 0) { "minSize must be positive" }
      require(maxSize >= 0) { "maxSize must be positive" }
   }

   override fun edgecases(): Iterable<Map<K, V>> = emptyList()

   override fun samples(random: Random): Sequence<PropertyInput<Map<K, V>>> {
      return generateSequence {
         val size = random.nextInt(minSize, maxSize)
         val map = keyArb.samples(random).zip(valueArb.samples(random))
            .map { (a, b) -> Pair(a.value, b.value) }
            .take(size)
            .toList()
            .toMap()
         PropertyInput(map, MapShrinker())
      }.take(iterations)
   }
}
