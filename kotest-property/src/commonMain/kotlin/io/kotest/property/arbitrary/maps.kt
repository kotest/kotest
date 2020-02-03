package io.kotest.property.arbitrary

import io.kotest.property.Shrinker

/**
 * Returns an [Arb] where each generated value is a map, with the entries of the map
 * drawn from the given pair generating arb. The size of each
 * generated map is a random value between the specified min and max bounds.
 *
 * There are no edgecases.
 *
 * This arbitrary uses a [Shrinker] which will reduce the size of a failing map by
 * removing elements from the failed case until it is empty.
 *
 * @see MapShrinker
 */
fun <K, V> Arb.Companion.map(
   arb: Arb<Pair<K, V>>,
   minSize: Int = 1,
   maxSize: Int = 100
): Arb<Map<K, V>> = arb(MapShrinker()) { rand ->
   val size = rand.nextInt(minSize, maxSize)
   val pairs = List(size) { arb.sample(rand).value }
   pairs.toMap()
}

/**
 * Returns an [Arb] where each generated value is a map, with the entries of the map
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
fun <K, V> Arb.Companion.map(
   keyArb: Arb<K>,
   valueArb: Arb<V>,
   minSize: Int = 1,
   maxSize: Int = 100
): Arb<Map<K, V>> {
   require(minSize >= 0) { "minSize must be positive" }
   require(maxSize >= 0) { "maxSize must be positive" }

   return arb(MapShrinker()) { random ->
      val size = random.nextInt(minSize, maxSize)
      val pairs = List(size) {
         keyArb.sample(random).value to valueArb.sample(random).value
      }
      pairs.toMap()
   }
}

class MapShrinker<K, V> : Shrinker<Map<K, V>> {
   override fun shrink(value: Map<K, V>): List<Map<K, V>> {
      return when (value.size) {
         0 -> emptyList()
         1 -> listOf(emptyMap())
         else -> listOf(
            value.toList().take(value.size / 2).toMap(),
            value.toList().drop(1).toMap()
         )
      }
   }
}
