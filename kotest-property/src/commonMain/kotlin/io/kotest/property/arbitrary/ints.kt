package io.kotest.property.arbitrary

import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.nextInt

fun Arb.Companion.int(min: Int, max: Int) = int(min..max)

fun Arb.Companion.int(range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE) =
   arb(IntShrinker, listOf(0, Int.MAX_VALUE, Int.MIN_VALUE)) { it.random.nextInt(range) }

/**
 * Returns an [Arb] where each value is a randomly chosen natural integer.
 * The edge cases are: [Int.MAX_VALUE]
 */
fun Arb.Companion.nats(max: Int = Int.MAX_VALUE) = int(1..max)

/**
 * Returns an [Arb] where each value is a randomly chosen negative integer.
 * The edge cases are: [Int.MIN_VALUE]
 */
fun Arb.Companion.negativeInts(min: Int = Int.MIN_VALUE) = int(min..0)

/**
 * Returns an [Arb] where each value is a randomly chosen positive integer.
 * The edge cases are: [Int.MAX_VALUE]
 */
fun Arb.Companion.positiveInts(max: Int = Int.MAX_VALUE) = int(0..max)

object IntShrinker : Shrinker<Int> {
   override fun shrink(value: Int): List<Int> =
      when (value) {
         0 -> emptyList()
         1, -1 -> listOf(0)
         else -> {
            val a = listOf(abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct()
               .filterNot { it == value }
         }
      }
}

fun <A, B> Shrinker<A>.bimap(f: (B) -> A, g: (A) -> B): Shrinker<B> = object : Shrinker<B> {
   override fun shrink(value: B): List<B> = this@bimap.shrink(f(value)).map(g)
}
