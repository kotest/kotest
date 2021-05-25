package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.nextInt
import kotlin.random.nextUInt

/**
 * Returns an [Arb] that produces [Int]s, where the edge cases are [min], -1, 0, 1 and [max].
 * -1, 0 and 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.int(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE) = int(min..max)

/**
 * Returns an [Arb] that produces [Int]s, where the edge cases are [IntRange.first], -1, 0, 1 and [IntRange.last].
 * -1, 0 and 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.int(range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE): Arb<Int> {
   val edgecases = intArrayOf(range.first, -1, 0, 1, range.last).filter { it in range }.distinct()
   return arbitrary(edgecases, IntShrinker(range)) { it.random.nextInt(range) }
}

/**
 * Returns an [Arb] that produces positives [Int]s,  where the edge cases are 1 and [max].
 */
fun Arb.Companion.positiveInts(max: Int = Int.MAX_VALUE) = int(1..max)

/**
 * Returns an [Arb] that produces natural [Int]s, excluding 0, where the edge cases are 1 and [max].
 */
fun Arb.Companion.nats(max: Int = Int.MAX_VALUE) = positiveInts(max)

/**
 * Returns an [Arb] that produces negative [Int]s, where the edge cases are [min] and -1.
 */
fun Arb.Companion.negativeInts(min: Int = Int.MIN_VALUE) = int(min until 0)

class IntShrinker(val range: IntRange) : Shrinker<Int> {
   override fun shrink(value: Int): List<Int> =
      when (value) {
         0 -> emptyList()
         1, -1 -> listOf(0).filter { it in range }
         else -> {
            val a = intArrayOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct()
               .filterNot { it == value }
               .filter { it in range }
         }
      }
}


/**
 * Returns an [Arb] that produces [Int]s, where the edge cases are [min], 1 and [max].
 * 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.uint(min: UInt = UInt.MIN_VALUE, max: UInt = UInt.MAX_VALUE) = uint(min..max)

/**
 * Returns an [Arb] that produces [Int]s, where the edge cases are [UIntRange.first], 1 and [UIntRange.last].
 * 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.uint(range: UIntRange = UInt.MIN_VALUE..UInt.MAX_VALUE): Arb<UInt> {
   val edges = listOf(range.first, 1u, range.last).filter { it in range }.distinct()
   return arbitrary(edges, UIntShrinker(range)) { it.random.nextUInt(range) }
}

class UIntShrinker(val range: UIntRange) : Shrinker<UInt> {
   override fun shrink(value: UInt): List<UInt> =
      when (value) {
         0u -> emptyList()
         1u -> listOf(0u).filter { it in range }
         else -> {
            val a = listOf(0u, 1u, value / 3u, value / 2u, value * 2u / 3u)
            val b = (1u..5u).map { value - it }.reversed().filter { it > 0u }
            (a + b).distinct()
               .filterNot { it == value }
               .filter { it in range }
         }
      }
}

fun <A, B> Shrinker<A>.bimap(f: (B) -> A, g: (A) -> B): Shrinker<B> = object : Shrinker<B> {
   override fun shrink(value: B): List<B> = this@bimap.shrink(f(value)).map(g)
}
