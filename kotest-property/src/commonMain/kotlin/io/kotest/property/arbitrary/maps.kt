package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Shrinker

/**
 * Returns an [Arb] where each generated value is a map, with the entries of the map
 * drawn from the given pair generating arb. The size of each
 * generated map is a random value between the specified min and max bounds.
 *
 * This arbitrary uses a [Shrinker] which will reduce the size of a failing map by
 * removing elements from the failed case until it is empty.
 *
 * @see MapShrinker
 *
 * @param arb the arbitrary to populate the map entries
 * @param minSize the desired minimum size of the generated map
 * @param maxSize the desired maximum size of the generated map
 * @param slippage when generating keys, we may have repeats if the underlying gen is random.
 *        The slippage factor determines how many times we continue after retrieving a duplicate key.
 *        The total acceptable number of misses is the slippage factor multiplied by the target set size.
 *        If this value is not specified, then the default slippage value of 10 will be used.
 */
fun <K, V> Arb.Companion.map(
   arb: Arb<Pair<K, V>>,
   minSize: Int = 0,
   maxSize: Int = 100,
   slippage: Int = 10
): Arb<Map<K, V>> {

   require(minSize >= 0) { "minSize must be positive" }
   require(maxSize >= 0) { "maxSize must be positive" }
   val edgecase = if (minSize == 0) listOf(emptyMap<K, V>()) else emptyList()

   return arbitrary(edgecase, MapShrinker(minSize)) { random ->
      val targetSize = random.random.nextInt(minSize, maxSize)
      val maxMisses = targetSize * slippage
      val map = mutableMapOf<K, V>()
      var iterations = 0
      while (iterations < maxMisses && map.size < targetSize) {
         val initialSize = map.size
         val (key, value) = arb.single(random)
         map[key] = value
         if (map.size == initialSize) iterations++
      }

      require(map.size >= minSize) {
         "the minimum size requirement of $minSize could not be satisfied after $iterations consecutive samples"
      }

      map
   }
}

/**
 * Returns an [Arb] where each generated value is a map, with the entries of the map
 * drawn by combining values from the key gen and value gen. The size of each
 * generated map is a random value between the specified min and max bounds.
 *
 * The edgecase of this map is an empty map which is only specified when minSize = 0.
 *
 * This arbitrary uses a [Shrinker] which will reduce the size of a failing map by
 * removing elements until they map is empty.
 *
 * @see MapShrinker
 *
 * @param keyArb the arbitrary to populate the keys
 * @param valueArb the arbitrary to populate the values
 * @param minSize the desired minimum size of the generated map
 * @param maxSize the desired maximum size of the generated map
 * @param slippage when generating keys, we may have repeats if the underlying gen is random.
 *        The slippage factor determines how many times we continue after retrieving a duplicate key.
 *        The total acceptable number of misses is the slippage factor multiplied by the target set size.
 *        If this value is not specified, then the default slippage value of 10 will be used.
 */
fun <K, V> Arb.Companion.map(
   keyArb: Arb<K>,
   valueArb: Arb<V>,
   minSize: Int = 0,
   maxSize: Int = 100,
   slippage: Int = 10
): Arb<Map<K, V>> = Arb.map(Arb.pair(keyArb, valueArb), minSize, maxSize, slippage)

class MapShrinker<K, V>(private val minSize: Int) : Shrinker<Map<K, V>> {
   override fun shrink(value: Map<K, V>): List<Map<K, V>> {
      val shrinks = when (value.size) {
         0 -> emptyList()
         1 -> listOf(emptyMap())
         else -> listOf(
            value.toList().take(value.size / 2).toMap(),
            value.toList().drop(1).toMap()
         )
      }

      return shrinks.filter { it.size >= minSize }
   }
}

/**
 * Returns an [Arb] that produces Pairs of K,V using the supplied arbs for K and V.
 * Edgecases will be derived from [k] and [v].
 */
fun <K, V> Arb.Companion.pair(k: Arb<K>, v: Arb<V>): Arb<Pair<K, V>> {
   val arbPairWithoutKeyEdges: Arb<Pair<K, V>> = Arb.bind(k.removeEdgecases(), v, ::Pair)
   val arbPairWithoutValueEdges: Arb<Pair<K, V>> = Arb.bind(k, v.removeEdgecases(), ::Pair)
   val arbPair: Arb<Pair<K, V>> = Arb.bind(k, v, ::Pair)
   return Arb.choice(arbPair, arbPairWithoutKeyEdges, arbPairWithoutValueEdges)
}
