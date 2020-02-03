package io.kotest.property.arbitrary

import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.nextLong

fun Arb.Companion.long(range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE): Arb<Long> {
   val edgecases = listOf(0, Long.MAX_VALUE, Long.MIN_VALUE)
   return arb(LongShrinker, edgecases) { it.nextLong(range) }
}

object LongShrinker : Shrinker<Long> {
   override fun shrink(value: Long): List<Long> =
      when (value) {
         0L -> emptyList()
         1L, -1L -> listOf(0L)
         else -> {
            val a = listOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct()
               .filterNot { it == value }
         }
      }
}
