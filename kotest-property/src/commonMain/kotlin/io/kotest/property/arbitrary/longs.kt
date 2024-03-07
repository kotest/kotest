package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.numbers.LongClassifier
import kotlin.math.abs
import kotlin.random.nextLong
import kotlin.random.nextULong

class LongShrinker(private val range: LongRange) : Shrinker<Long> {
   override fun shrink(value: Long): List<Long> = when (value) {
      0L -> emptyList()
      1L, -1L -> listOf(0L).filter { it in range }
      else -> {
         val a = listOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
         val b = (1..5).map { value - it }.reversed().filter { it > 0 }
         (a + b).distinct().filterNot { it == value }.filter { it in range }
      }
   }
}

/**
 * Returns an [Arb] that produces [Long]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1, 0, 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.long(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = long(min..max)

/**
 * Returns an [Arb] that produces [Long]s in [range].
 *
 * The edge cases are [LongRange.first], -1, 0, 1 and [LongRange.last].
 *
 * -1, 0, and 1 are only included if they are present in the given range.
 */
fun Arb.Companion.long(range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE): Arb<Long> {
   val edgeCases = longArrayOf(range.first, -1, 0, 1, range.last).filter { it in range }.distinct()
   return ArbitraryBuilder.create { it.random.nextLong(range) }
      .withEdgecases(edgeCases)
      .withShrinker(LongShrinker(range))
      .withClassifier(LongClassifier(range))
      .build()
}

/**
 * Returns an [Arb] that produces positive [Long]s from 1 to [max] (inclusive).
 * The edge cases are 1 and [max].
 */
fun Arb.Companion.positiveLong(max: Long = Long.MAX_VALUE): Arb<Long> = long(1L, max)

/**
 * Returns an [Arb] that produces non-negative [Long]s from 0 to [max] (inclusive).
 * The edge cases are 0, 1 and [max].
 *
 * Max defaults to [Long.MAX_VALUE]
 */
fun Arb.Companion.nonNegativeLong(max: Long = Long.MAX_VALUE) = long(0, max)

/**
 * Returns an [Arb] that produces negative [Long]s from [min] to -1 (inclusive).
 * The edge cases are [min] and -1.
 */
fun Arb.Companion.negativeLong(min: Long = Long.MIN_VALUE): Arb<Long> = long(min, -1L)

/**
 * Returns an [Arb] that produces non-positive [Long]s from [min] to 0 (inclusive).
 * The edge cases are [min], -1 and 0.
 */
fun Arb.Companion.nonPositiveLong(min: Long = Long.MIN_VALUE) = long(min, 0)

/**
 * Returns an [Arb] that produces [LongArray]s where [generateArrayLength] produces the length of the arrays and
 * [generateContents] produces the content of the arrays.
 */
fun Arb.Companion.longArray(generateArrayLength: Gen<Int>, generateContents: Arb<Long>): Arb<LongArray> =
   toPrimitiveArray(generateArrayLength, generateContents, Collection<Long>::toLongArray)

class ULongShrinker(val range: ULongRange) : Shrinker<ULong> {
   override fun shrink(value: ULong): List<ULong> = when (value) {
      0uL -> emptyList()
      1uL -> listOf(0uL).filter { it in range }
      else -> {
         val a = listOf(0uL, 1uL, value / 3u, value / 2u, value * 2u / 3u)
         val b = (1u..5u).map { value - it }.reversed().filter { it > 0u }
         (a + b).distinct().filterNot { it == value }.filter { it in range }
      }
   }
}

/**
 * Returns an [Arb] that produces [ULong]s from [min] to [max] (inclusive).
 * The edge cases are [min], 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.uLong(min: ULong = ULong.MIN_VALUE, max: ULong = ULong.MAX_VALUE) = uLong(min..max)

/**
 * Returns an [Arb] that produces [ULong]s in range.
 * The edge cases are [ULongRange.first], 1 and [ULongRange.last] which are only included if they are in the provided
 * range.
 */
fun Arb.Companion.uLong(range: ULongRange = ULong.MIN_VALUE..ULong.MAX_VALUE): Arb<ULong> {
   val edgeCases = listOf(range.first, 1uL, range.last).filter { it in range }.distinct()
   return arbitrary(edgeCases, ULongShrinker(range)) {
      var value = it.random.nextULong(range)
      while(value !in range) value = it.random.nextULong(range) // temporary workaround for KT-47304
      value
   }
}

/**
 * Returns an [Arb] that produces [ULongArray]s where [generateArrayLength] produces the length of the arrays and
 * [generateContents] produces the content of the arrays.
 */
@ExperimentalUnsignedTypes
fun Arb.Companion.uLongArray(generateArrayLength: Gen<Int>, generateContents: Arb<ULong>): Arb<ULongArray> =
   toPrimitiveArray(generateArrayLength, generateContents, Collection<ULong>::toULongArray)
