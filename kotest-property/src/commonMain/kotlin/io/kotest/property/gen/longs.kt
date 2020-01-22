package io.kotest.property.gen

import io.kotest.property.Gen
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextLong

fun Gen.Companion.long(range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE) = object : Gen<Long> {
   override fun edgecases(): Iterable<Long> = listOf(Long.MIN_VALUE, Long.MAX_VALUE, 0)
   override fun generate(random: Random): Long = random.nextLong(range)
   override fun shrinker(): Shrinker<Long>? = LongShrinker
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
