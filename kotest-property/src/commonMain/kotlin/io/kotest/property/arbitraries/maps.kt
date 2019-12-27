package io.kotest.property.arbitraries

import io.kotest.property.*
import kotlin.random.Random

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
): Gen<Map<K, V>> = object :
   Arbitrary<Map<K, V>> {

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
): Gen<Map<K, V>> = object :
   Arbitrary<Map<K, V>> {

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

class MapShrinker<K, V> : Shrinker<Map<K, V>> {
   override fun shrink(value: Map<K, V>): List<PropertyInput<Map<K, V>>> {
      return when (value.size) {
         0 -> emptyList()
         1 -> listOf(PropertyInput(emptyMap()))
         else -> listOf(
            PropertyInput(value.toList().take(value.size / 2).toMap(), this),
            PropertyInput(value.toList().drop(1).toMap(), this)
         )
      }
   }
}
