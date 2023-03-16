package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.RandomSource
import kotlin.random.nextInt

fun <K, A, B> Arb.Companion.map2(
   genK: Gen<K>,
   genA: Gen<A>,
   genB: Gen<B>,
   size: IntRange = 0..1000,
   slippage: Int = 10,
   shared: IntRange = 0..100
   ): Arb<Maps2Result<K, A, B>> {

   if (genK is Exhaustive) {
      require(size.last <= genK.values.size) {
         "Size should be <= ${genK.values.size}"
      }
   }

   size.requireBetween(0, Int.MAX_VALUE)
   shared.requireBetween(0, 100)

   val lShrinker = MapShrinker<K, A>(size.first)
   val rShrinker = MapShrinker<K, B>(size.first)

   return ArbitraryBuilder.create { random ->

      val (kk, aa, bb) = buildIterators(random, genK, genA, genB)
      val (lSize, rSize) = random.nextSizePair(size)
      val smallerSize = minOf(lSize, rSize)

      // calc number of keys both maps should share
      val sharedKeysPercent = random.random.nextInt(shared)
      val sharedKeysCount = (smallerSize / 100.0 * sharedKeysPercent).toInt()

      // create two equally sized maps that share all keys
      val ls1 = buildMap(sharedKeysCount, kk, aa, slippage = slippage)
      val rs1 = ls1.keys.associateWith { bb.next() }

      // add additional entries (not sharing keys) to reach expected size
      val ls2 = buildMap(lSize, kk, aa, slippage, ls1)
      val rs2 = buildMap(rSize, genK, bb, ls2.keys, random, slippage, rs1)

      Maps2Result(ls2, rs2)
   }
      .withEdgecaseFn { random ->

         val (kk, aa, bb) = buildIterators(random, genK, genA, genB)

         when (random.random.nextInt(7)) {
            0 -> {
               // both sides min size. no shared keys
               val ls = buildMap(size.first, kk, aa, slippage = slippage)
               val rs = buildMap(size.first, genK, bb, ls.keys, random, slippage = slippage)

               Maps2Result(ls, rs)
            }

            1 -> {
               // left side min size, right side max size. no shared keys
               val ls = buildMap(size.first, kk, aa, slippage = slippage)
               val rs = buildMap(size.last, genK, bb, ls.keys, random, slippage = slippage)

               Maps2Result(ls, rs)
            }

            2 -> {
               // left side max size, right side min size. no shared keys
               val ls = buildMap(size.last, kk, aa, slippage = slippage)
               val rs = buildMap(size.first, genK, bb, ls.keys, random, slippage = slippage)

               Maps2Result(ls, rs)
            }

            3 -> {
               // both maps max size, all keys shared
               val ls = buildMap(size.last, kk, aa, slippage = slippage)
               val rs = ls.keys.associateWith { bb.next() }

               Maps2Result(ls, rs)
            }

            4 -> {
               // mid size, all keys shared
               val size = random.random.nextInt(size)
               val ls = buildMap(size, kk, aa, slippage = slippage)
               val rs = ls.keys.associateWith { bb.next() }

               Maps2Result(ls, rs)
            }

            5 -> {
               // min size, all keys shared
               val ls = buildMap(size.first, kk, aa, slippage = slippage)
               val rs = ls.keys.associateWith { bb.next() }

               Maps2Result(ls, rs)
            }

            6 -> {
               // both max size, no keys shared
               val ls = buildMap(size.last, kk, aa, slippage = slippage)
               val rs = buildMap(size.last, genK, bb, ls.keys, random, slippage = slippage)

               Maps2Result(ls, rs)
            }

            else -> throw AssertionError("should never happen")
         }
      }.withShrinker { sample ->
         lShrinker.shrink(sample.left).map { Maps2Result(it, sample.right) } +
            rShrinker.shrink(sample.right).map { Maps2Result(sample.left, it) } +
            lShrinker.shrink(sample.left).flatMap { ls -> rShrinker.shrink(sample.right).map { Maps2Result(ls, it) } }
      }.withClassifier {
         it.classify(size.first, size.last)
      }
      .build()
}

private fun RandomSource.nextSizePair(sizeRange: IntRange): Pair<Int, Int> {
   val size1 = random.nextInt(sizeRange)
   val size2 = random.nextInt(IntRange(sizeRange.first, size1))

   // choose by random which side should have more overall entries
   return if (random.nextBoolean()) {
      size1 to size2
   } else {
      size2 to size1
   }
}

private fun <A, B, C> buildIterators(
   random: RandomSource,
   a: Gen<A>,
   b: Gen<B>,
   c: Gen<C>
): Triple<Iterator<A>, Iterator<B>, Iterator<C>> =
   Triple(
      a.generate(random).map { it.value }.iterator(),
      b.generate(random).map { it.value }.iterator(),
      c.generate(random).map { it.value }.iterator()
   )

private fun <K, V> buildMap(
   withSize: Int,
   keys: Iterator<K>,
   values: Iterator<V>,
   slippage: Int,
   map: Map<K, V> = emptyMap()
): Map<K, V> =
   map.fillToSize(withSize, slippage) {
      keys.next() to values.next()
   }

private fun <K, V> buildMap(
   withSize: Int,
   keys: Gen<K>,
   values: Iterator<V>,
   excludeKeys: Set<K>,
   random: RandomSource,
   slippage: Int,
   map: Map<K, V> = emptyMap(),
): Map<K, V> {
   val validKeys = keys.generate(random).filter { !excludeKeys.contains(it.value) }.iterator()
   return map.fillToSize(withSize, slippage) {
      validKeys.next().value to values.next()
   }
}

private fun <K, V> Map<K, V>.fillToSize(expectedSize: Int, slippage: Int, nextEntry: () -> Pair<K, V>): Map<K, V> =
   buildMap {
      val maxMisses = expectedSize * slippage

      putAll(this@fillToSize)

      var iterations = 0
      while (iterations < maxMisses && size < expectedSize) {
         iterations++
         val entry = nextEntry()
         val (k, v) = entry
         this[k] = v
      }
   }

private fun IntRange.requireBetween(min: Int, max: Int) {
   require(this.first >= min)
   require(this.last <= max)
}

data class Maps2Result<K, A, B>(val left: Map<K, A>, val right: Map<K, B>) {
   internal fun classify(minSize: Int, maxSize: Int): String {
      val intersect = left.keys.intersect(right.keys)
      val allSharedKeys = (intersect.size == left.size && intersect.size == right.size)
      val noSharedKeys = intersect.isEmpty()

      val overlap = if (allSharedKeys) {
         " / all keys shared"
      } else if (noSharedKeys) {
         " / no keys shared"
      } else {
         " / some keys shared"
      }

      return left.classify(minSize, maxSize) + " / " + right.classify(minSize, maxSize) + overlap
   }

   private fun <K, A> Map<K, A>.classify(minSize: Int, maxSize: Int) =
      when (size) {
         0 -> "empty    "
         minSize -> "min sized"
         maxSize -> "max sized"
         else -> "mid sized"
      }
}
