package io.kotest.property.gen

import io.kotest.property.Gen
import io.kotest.property.Shrinker
import io.kotest.property.setEdgeCases
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextInt

fun Gen.Companion.int(range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE) = object : Gen<Int> {
   override fun generate(random: Random): Int = random.nextInt(range)
   override fun edgecases(): Iterable<Int> = listOf(Int.MIN_VALUE, Int.MAX_VALUE, 0)
}

/**
 * Returns a [Gen] where each value is a randomly chosen positive integer.
 * The edge cases are: [Int.MAX_VALUE]
 */
fun Gen.Companion.positiveInts() = Gen.int(0..Int.MAX_VALUE).setEdgeCases(Int.MAX_VALUE)

/**
 * Returns a [Gen] where each value is a randomly chosen negative integer.
 * The edge cases are: [Int.MIN_VALUE]
 */
fun Gen.Companion.negativeInts(): Gen<Int> = Gen.int(Int.MIN_VALUE..0).setEdgeCases(Int.MIN_VALUE)

/**
 * Returns a [Gen] where each value is a randomly chosen natural number.
 * The edge cases are: [Int.MAX_VALUE]
 */
fun Gen.Companion.nats(): Gen<Int> = Gen.int(1..Int.MAX_VALUE).setEdgeCases(Int.MAX_VALUE)

object IntShrinker : Shrinker<Int> {
   override fun shrink(value: Int): List<Int> =
      when (value) {
         0 -> emptyList()
         1, -1 -> listOf(0)
         else -> {
            val a = listOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct().filterNot { it == value }
         }
      }
}
