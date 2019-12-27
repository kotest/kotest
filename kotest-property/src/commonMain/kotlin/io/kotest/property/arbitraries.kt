package io.kotest.property

import io.kotest.properties.nextPrintableString
import io.kotest.property.arbitraries.int
import io.kotest.property.shrinker.*
import kotlin.random.Random
import kotlin.random.nextLong

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
 *
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
