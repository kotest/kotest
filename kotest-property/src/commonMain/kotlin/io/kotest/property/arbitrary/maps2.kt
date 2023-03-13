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
   sharedKeyPercentage: IntRange = 0..100,
   sizeRange: IntRange = 0..1000,
): Arb<Maps2Result<K, A, B>> {

   if (genK is Exhaustive) {
      require(sizeRange.last <= genK.values.size) {
         "Size should be <= ${genK.values.size}"
      }
   }

   sizeRange.requireBetween(0, Int.MAX_VALUE)
   sharedKeyPercentage.requireBetween(0, 100)

   val lsShrinker = MapShrinker<K, A>(sizeRange.first)
   val rsShrinker = MapShrinker<K, B>(sizeRange.first)

   return ArbitraryBuilder.create { random ->

      val (kk, aa, bb) = buildIterators(random, genK, genA, genB)

      val size1 = random.random.nextInt(sizeRange)
      val size2 = random.random.nextInt(IntRange(sizeRange.first, size1))
      val smallerSize = if (size1 > size2) size2 else size1

      // choose by random which side should have more overall entries
      val (sizeA, sizeB) = if (random.random.nextBoolean()) {
         size1 to size2
      } else {
         size2 to size1
      }

      // calc number of keys both maps should share
      val sharedKeysPercentage = random.random.nextInt(sharedKeyPercentage)
      val sharedKeysCount = (smallerSize / 100.0 * sharedKeysPercentage).toInt()

      // step #1 create two equally sized maps that share all keys
      val ls1 = buildMap(sharedKeysCount, kk, aa)
      val rs1 = ls1.keys.associateWith { bb.next() }

      // step #2 add additional entries (not sharing keys) to reach expected size
      val ls2 = buildMap(sizeA, kk, aa, ls1)
      val rs2 = buildMap(sizeB, genK, bb, ls2.keys, random, rs1)

      Maps2Result(ls2, rs2)
   }.withEdgecaseFn { random ->

      val (kk, aa, bb) = buildIterators(random, genK, genA, genB)

      when (random.random.nextInt(7)) {
         0 -> {
            // both sides min size. no shared keys
            val ls = buildMap(sizeRange.first, kk, aa)
            val rs = buildMap(sizeRange.first, genK, bb, ls.keys, random)

            Maps2Result(ls, rs)
         }

         1 -> {
            // left side min size, right side max size. no shared keys
            val ls = buildMap(sizeRange.first, kk, aa)
            val rs = buildMap(sizeRange.last, genK, bb, ls.keys, random)

            Maps2Result(ls, rs)
         }

         2 -> {
            // left side max size, right side min size. no shared keys
            val ls = buildMap(sizeRange.last, kk, aa)
            val rs = buildMap(sizeRange.first, genK, bb, ls.keys, random)

            Maps2Result(ls, rs)
         }

         3 -> {
            // both maps max size, all keys shared
            val ls = buildMap(sizeRange.last, kk, aa)
            val rs = ls.keys.associateWith { bb.next() }

            Maps2Result(ls, rs)
         }

         4 -> {
            // mid size, all keys shared
            val size = random.random.nextInt(sizeRange)
            val ls = buildMap(size, kk, aa)
            val rs = ls.keys.associateWith { bb.next() }

            Maps2Result(ls, rs)
         }

         5 -> {
            // min size, all keys shared
            val ls = buildMap(sizeRange.first, kk, aa)
            val rs = ls.keys.associateWith { bb.next() }

            Maps2Result(ls, rs)
         }

         6 -> {
            // both max size, no keys shared
            val ls = buildMap(sizeRange.last, kk, aa)
            val rs = buildMap(sizeRange.last, genK, bb, ls.keys, random)

            Maps2Result(ls, rs)
         }

         else -> throw AssertionError("should never happen")
      }
   }.withShrinker { sample ->
      lsShrinker.shrink(sample.left).map { Maps2Result(it, sample.right) } +
         rsShrinker.shrink(sample.right).map { Maps2Result(sample.left, it) } +
         lsShrinker.shrink(sample.left).flatMap { ls -> rsShrinker.shrink(sample.right).map { Maps2Result(ls, it) } }
   }.withClassifier {
      it.classify(sizeRange.first, sizeRange.last)
   }
      .build()
}

private fun <A,B,C> buildIterators(random: RandomSource, a: Gen<A>, b: Gen<B>, c: Gen<C>): Triple<Iterator<A>, Iterator<B>, Iterator<C>> =
   Triple(
      a.generate(random).map { it.value }.iterator(),
      b.generate(random).map { it.value }.iterator(),
      c.generate(random).map { it.value }.iterator()
   )

private fun <K, A> Map<K, A>.classify(minSize: Int, maxSize: Int) =
   when (size) {
      0 -> "empty    "
      minSize -> "min sized"
      maxSize -> "max sized"
      else -> "mid sized"
   }

private fun <K, A, B> Maps2Result<K, A, B>.classify(minSize: Int, maxSize: Int): String {
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

private fun <K, V> buildMap(
   withSize: Int,
   keys: Iterator<K>,
   values: Iterator<V>,
   map: Map<K, V> = emptyMap()
): Map<K, V> =
   map.fillToSize(withSize) {
      keys.next() to values.next()
   }

private fun <K, V> buildMap(
   withSize: Int,
   keys: Gen<K>,
   values: Iterator<V>,
   excludeKeys: Set<K>,
   random: RandomSource,
   map: Map<K,V> = emptyMap()
): Map<K, V> {
   val validKeys = keys.generate(random).filter { !excludeKeys.contains(it.value) }.iterator()
   return map.fillToSize(withSize) {
      validKeys.next().value to values.next()
   }
}

private fun <K, V> Map<K, V>.fillToSize(expectedSize: Int, nextEntry: () -> Pair<K, V>): Map<K, V> =
   buildMap {
      putAll(this@fillToSize)

      // TODO: there is a chance this loops runs forever if we cannot fill the map to expected size
      // when nextEntry() produces a limited number of unique keys
      // therefor we are limiting the number of iterations here. maybe there is a more elegant solution?
      val maxIterations = expectedSize * 2
      var iterations = 0
      while (size < expectedSize && iterations < maxIterations) {
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

data class Maps2Result<K, A, B>(val left: Map<K, A>, val right: Map<K, B>)
