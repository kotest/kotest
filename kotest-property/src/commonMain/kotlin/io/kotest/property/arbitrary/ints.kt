package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.nextInt
import kotlin.random.nextUInt

fun <A, B> Shrinker<A>.bimap(f: (B) -> A, g: (A) -> B): Shrinker<B> = object : Shrinker<B> {
   override fun shrink(value: B): List<B> = this@bimap.shrink(f(value)).map(g)
}

class IntShrinker(val range: IntRange) : Shrinker<Int> {
   override fun shrink(value: Int): List<Int> = when (value) {
      0 -> emptyList()
      1, -1 -> listOf(0).filter { it in range }
      else -> {
         val a = intArrayOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
         val b = (1..5).map { value - it }.reversed().filter { it > 0 }
         (a + b).distinct().filterNot { it == value }.filter { it in range }
      }
   }
}

/**
 * Returns an [Arb] that produces [Int]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1, 0, 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.int(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE) = int(min..max)

/**
 * Returns an [Arb] that produces [Int]s in [range].
 * The edge cases are [IntRange.first], -1, 0, 1 and [IntRange.last] which are only included if they are in the provided
 * range.
 */
fun Arb.Companion.int(range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE): Arb<Int> {
   val edgeCases = intArrayOf(range.first, -1, 0, 1, range.last).filter { it in range }.distinct()
   return arbitrary(edgeCases, IntShrinker(range)) { it.random.nextInt(range) }
}

/**
 * Returns an [Arb] that produces positive [Int]s from 1 to [max] (inclusive).
 * The edge cases are 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.positiveInt(max: Int = Int.MAX_VALUE) = int(1, max)

@Deprecated("use positiveInt", ReplaceWith("positiveInt(max)"))
fun Arb.Companion.positiveInts(max: Int = Int.MAX_VALUE) = positiveInt(max)

@Deprecated("use positiveInt", ReplaceWith("positiveInt(max)"))
fun Arb.Companion.nats(max: Int = Int.MAX_VALUE) = positiveInt(max)

/**
 * Returns an [Arb] that produces non negative [Int]s from 0 to [max] (inclusive).
 * The edge cases are 0, 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.nonNegativeInt(max: Int = Int.MAX_VALUE) = int(0, max)

/**
 * Returns an [Arb] that produces negative [Int]s from [min] to -1 (inclusive).
 * The edge cases are [min] and -1 which are only included if they are in the provided range.
 */
fun Arb.Companion.negativeInt(min: Int = Int.MIN_VALUE) = int(min, -1)

@Deprecated("use negativeInt", ReplaceWith("negativeInt(min)"))
fun Arb.Companion.negativeInts(min: Int = Int.MIN_VALUE) = negativeInt(min)

/**
 * Returns an [Arb] that produces non positive [Int]s from [min] to 0 (inclusive).
 * The edge cases are [min], -1 and 0 which are only included if they are in the provided range.
 */
fun Arb.Companion.nonPositiveInt(min: Int = Int.MIN_VALUE) = int(min, 0)

/**
 * Returns an [Arb] that produces [IntArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
fun Arb.Companion.intArray(length: Gen<Int>, content: Arb<Int>): Arb<IntArray> =
   toPrimitiveArray(length, content, Collection<Int>::toIntArray)

class UIntShrinker(val range: UIntRange) : Shrinker<UInt> {
   override fun shrink(value: UInt): List<UInt> = when (value) {
      0u -> emptyList()
      1u -> listOf(0u).filter { it in range }
      else -> {
         val a = listOf(0u, 1u, value / 3u, value / 2u, value * 2u / 3u)
         val b = (1u..5u).map { value - it }.reversed().filter { it > 0u }
         (a + b).distinct().filterNot { it == value }.filter { it in range }
      }
   }
}

/**
 * Returns an [Arb] that produces [UInt]s from [min] to [max] (inclusive).
 * The edge cases are [min], 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.uInt(min: UInt = UInt.MIN_VALUE, max: UInt = UInt.MAX_VALUE) = uInt(min..max)

/**
 * Returns an [Arb] that produces [UInt]s in range.
 * The edge cases are [UIntRange.first], 1 and [UIntRange.last] which are only included if they are in the provided
 * range.
 */
fun Arb.Companion.uInt(range: UIntRange = UInt.MIN_VALUE..UInt.MAX_VALUE): Arb<UInt> {
   val edges = listOf(range.first, 1u, range.last).filter { it in range }.distinct()
   return arbitrary(edges, UIntShrinker(range)) { it.random.nextUInt(range) }
}

/**
 * Returns an [Arb] that produces [UIntArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
@ExperimentalUnsignedTypes
fun Arb.Companion.uIntArray(length: Gen<Int>, content: Arb<UInt>): Arb<UIntArray> =
   toPrimitiveArray(length, content, Collection<UInt>::toUIntArray)
