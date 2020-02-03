package io.kotest.property.arbitrary

import io.kotest.property.Shrinker
import io.kotest.property.Sample
import io.kotest.property.sampleOf
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextLong

fun Arb.Companion.longs(range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE) = object : Arb<Long> {
   override fun sample(random: Random): Sample<Long> = sampleOf(random.nextLong(range), LongShrinker)
   override fun edgecases(): List<Long> = listOf(0, Long.MAX_VALUE, Long.MIN_VALUE)
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
