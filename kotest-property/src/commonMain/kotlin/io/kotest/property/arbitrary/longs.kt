package io.kotest.property.arbitrary

import io.kotest.property.Arbitrary
import io.kotest.property.PropertyInput
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextLong

fun Arbitrary.Companion.long(
   iterations: Int,
   range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE
) = object : Arbitrary<Long> {
   override fun edgecases(): Iterable<Long> = listOf(Long.MIN_VALUE, Long.MAX_VALUE, 0)
   override fun samples(random: Random): Sequence<PropertyInput<Long>> {
      return sequence {
         for (k in 0 until iterations) {
            val next = random.nextLong(range)
            val input = PropertyInput(next, LongShrinker)
            yield(input)
         }
      }
   }
}

object LongShrinker : Shrinker<Long> {
   override fun shrink(value: Long): List<PropertyInput<Long>> =
      when (value) {
         0L -> emptyList()
         1L, -1L -> listOf(PropertyInput(0L))
         else -> {
            val a = listOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct()
               .filterNot { it == value }
               .map { PropertyInput(it, this) }
         }
      }
}
