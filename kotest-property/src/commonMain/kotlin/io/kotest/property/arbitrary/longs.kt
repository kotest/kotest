package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.nextLong
import kotlin.random.nextULong

/**
 * Returns an [Arb] that produces [Long]s, where the edge cases are [min], -1, 0, 1 and [max].
 * -1, 0 and 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.long(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = long(min..max)

/**
 * Returns an [Arb] that produces [Long]s, where the edge cases are [IntRange.first], -1, 0, 1 and [IntRange.last].
 * -1, 0 and 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.long(range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE): Arb<Long> {
   val edgecases = longArrayOf(range.first, -1, 0, 1, range.last).filter { it in range }.distinct()
   return arbitrary(edgecases, LongShrinker(range)) { it.random.nextLong(range) }
}

class LongShrinker(private val range: LongRange) : Shrinker<Long> {
   override fun shrink(value: Long): List<Long> =
      when (value) {
         0L -> emptyList()
         1L, -1L -> listOf(0L).filter { it in range }
         else -> {
            val a = listOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct()
               .filterNot { it == value }
               .filter { it in range }
         }
      }
}

/**
 * Returns an [Arb] that produces [Long]s, where the edge cases are [min], 1 and [max].
 * 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.ulong(min: ULong = ULong.MIN_VALUE, max: ULong = ULong.MAX_VALUE) = ulong(min..max)

/**
 * Returns an [Arb] that produces [Long]s, where the edge cases are [IntRange.first], 1 and [IntRange.last].
 * 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.ulong(range: ULongRange = ULong.MIN_VALUE..ULong.MAX_VALUE): Arb<ULong> {
   val edgecases = listOf(range.first, 1uL, range.last).filter { it in range }.distinct()
   return arbitrary(edgecases, ULongShrinker(range)) { it.random.nextULong(range) }
}

class ULongShrinker(val range: ULongRange) : Shrinker<ULong> {
   override fun shrink(value: ULong): List<ULong> =
      when (value) {
         0uL -> emptyList()
         1uL -> listOf(0uL).filter { it in range }
         else -> {
            val a = listOf(0uL, 1uL, value / 3u, value / 2u, value * 2u / 3u)
            val b = (1u..5u).map { value - it }.reversed().filter { it > 0u }
            (a + b).distinct()
               .filterNot { it == value }
               .filter { it in range }
         }
      }
}
