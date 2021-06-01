package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.nextLong
import kotlin.random.nextULong

/**
 * Returns an [Arb] that produces [Long]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1, 0, 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.long(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = long(min..max)

/**
 * Returns an [Arb] that produces [Long]s in [range].
 * The edge cases are [LongRange.first], -1, 0, 1 and [LongRange.last] which are only included if they are in the
 * provided range.
 */
fun Arb.Companion.long(range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE): Arb<Long> {
   val edgecases = longArrayOf(range.first, -1, 0, 1, range.last).filter { it in range }.distinct()
   return arbitrary(edgecases, LongShrinker(range)) { it.random.nextLong(range) }
}

/**
 * Returns an [Arb] that produces positive [Long]s from 1 to [max] (inclusive).
 * The edge cases are 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.positiveLong(max: Long = Long.MAX_VALUE): Arb<Long> = long(1L, max)

/**
 * Returns an [Arb] that produces negative [Long]s from [min] to -1 (inclusive).
 * The edge cases are [min] and -1 which are only included if they are in the provided range.
 */
fun Arb.Companion.negativeLong(min: Long = Long.MIN_VALUE): Arb<Long> = long(min, -1L)

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
 * Returns an [Arb] that produces [ULong]s from [min] to [max] (inclusive).
 * The edge cases are [min], 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.ulong(min: ULong = ULong.MIN_VALUE, max: ULong = ULong.MAX_VALUE) = ulong(min..max)

/**
 * Returns an [Arb] that produces [ULong]s in range.
 * The edge cases are [ULongRange.first], 1 and [ULongRange.last] which are only included if they are in the provided
 * range.
 */
fun Arb.Companion.ulong(range: ULongRange = ULong.MIN_VALUE..ULong.MAX_VALUE): Arb<ULong> {
   val edgecases = listOf(range.first, 1uL, range.last).filter { it in range }.distinct()
   return arbitrary(edgecases, ULongShrinker(range)) { it.random.nextULong(range) }
}

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
