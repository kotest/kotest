package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import kotlin.random.nextInt

fun <K, A, B> Arb.Companion.map2(
   keyArb: Arb<K>,
   arbA: Arb<A>,
   arbB: Arb<B>,
   sharedKeyPercentage: IntRange = 0..100,
   sizeRange: IntRange = 0..1000,
): Arb<Pair<Map<K, A>, Map<K, B>>> {

   sizeRange.requireBetween(0, Int.MAX_VALUE)
   sharedKeyPercentage.requireBetween(0, 100)

   val leftShrinker = MapShrinker<K, A>(sizeRange.first)
   val rightShrinker = MapShrinker<K, B>(sizeRange.first)

   return ArbitraryBuilder.create { random ->
      val size1 = random.random.nextInt(sizeRange)
      val size2 = random.random.nextInt(IntRange(sizeRange.first, size1))
      val smallerSize = if (size1 > size2) size2 else size1

      // choose by random which side should have more overall entries
      val (sizeA, sizeB) = if (random.random.nextBoolean()) {
         size1 to size2
      } else {
         size2 to size1
      }

      // step #1 create two equally sized maps that share all keys
      val sharedKeysPercentage = random.random.nextInt(sharedKeyPercentage)
      val sharedKeysCount = (smallerSize / 100.0 * sharedKeysPercentage).toInt()

      val ls1 = emptyMap<K, A>().populate(sharedKeysCount, keyArb, arbA, random)
      val rs1 = ls1.keys.associateWith { arbB.next(random) }

      // step #2 add additional entries (not sharing keys) to reach expected size
      val ls2 = ls1.populate(sizeA, keyArb, arbA, random)
      val rs2 = rs1.populateIgnoring(sizeB, keyArb, arbB, ls2.keys, random)

      ls2 to rs2
   }.withEdgecaseFn { random ->

      when (random.random.nextInt(4)) {
         0 -> {
            // both sides min, no shared keys
            val ls = emptyMap<K, A>().populate(sizeRange.first, keyArb, arbA, random)
            val rs = emptyMap<K, B>().populateIgnoring(sizeRange.first, keyArb, arbB, ls.keys, random)

            ls to rs
         }

         1 -> {
            // left side min size, right side max size. no shared keys
            val ls = emptyMap<K, A>().populate(sizeRange.first, keyArb, arbA, random)
            val rs = emptyMap<K, B>().populateIgnoring(sizeRange.last, keyArb, arbB, ls.keys, random)

            ls to rs
         }

         2 -> {
            // left side max size, right side min size, no shared keys
            val ls = emptyMap<K, A>().populate(sizeRange.last, keyArb, arbA, random)
            val rs = emptyMap<K, B>().populateIgnoring(sizeRange.first, keyArb, arbB, ls.keys, random)

            ls to rs
         }

         3 -> {
            // both maps max size, all keys shared
            val ls = emptyMap<K, A>().populate(sizeRange.last, keyArb, arbA, random)
            val rs = ls.keys.associateWith { arbB.next(random) }

            ls to rs
         }

         else -> throw AssertionError("should never happen")
      }
   }.withShrinker { sample ->
      leftShrinker.shrink(sample.first).map { it to sample.second } +
      rightShrinker.shrink(sample.second).map { sample.first to it } +
      leftShrinker.shrink(sample.first).flatMap { ls -> rightShrinker.shrink(sample.second).map { ls to it } }
   }
      .build()
}

private fun <K, V> Map<K, V>.populate(
   expectedSize: Int,
   keys: Arb<K>,
   values: Arb<V>,
   random: RandomSource
): Map<K, V> =
   this.ensureSize(expectedSize) {
      keys.next(random) to values.next(random)
   }

private fun <K, V> Map<K, V>.populateIgnoring(
   expectedSize: Int,
   keys: Arb<K>,
   values: Arb<V>,
   ignoring: Set<K>,
   random: RandomSource
): Map<K, V> {
   val filteredKeys = keys.generate(random).filter { !ignoring.contains(it.value) }.iterator()
   return ensureSize(expectedSize) {
      filteredKeys.next().value to values.next(random)
   }
}

private fun <K, V> Map<K, V>.ensureSize(expectedSize: Int, nextEntry: () -> Pair<K, V>?): Map<K, V> =
   buildMap {
      putAll(this@ensureSize)

      val maxIterations = expectedSize * 2
      var iterations = 0
      while (this.size < expectedSize && iterations < maxIterations) {
         iterations++
         val entry = nextEntry() ?: break

         val (k, v) = entry
         this[k] = v
      }
   }

private fun IntRange.requireBetween(min: Int, max: Int) {
   require(this.first >= min)
   require(this.last <= max)
}
