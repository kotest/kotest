package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.nextLong

/**
 * Returns an [Arb] where each value is a randomly chosen [Long] between min and max.
 * The edgecases are: [[Long.MIN_VALUE], [Long.MAX_VALUE], 0, 1, -1] with any not in the
 * given bounds filtered out.
 */
fun Arb.Companion.long(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = long(min..max)

/**
 * Returns an [Arb] where each value is a randomly chosen [Long] in the given range.
 * The edgecases are: [[Long.MIN_VALUE], [Long.MAX_VALUE], 0, 1, -1] with any not in the
 * given range filtered out.
 */
fun Arb.Companion.long(range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE): Arb<Long> {
   val edgecases = listOf(0, 1, -1, Long.MAX_VALUE, Long.MIN_VALUE).filter { it in range }
   return arb(LongShrinker(range), edgecases) { it.random.nextLong(range) }
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
