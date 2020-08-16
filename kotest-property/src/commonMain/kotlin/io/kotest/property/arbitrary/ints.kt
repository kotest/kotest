package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Shrinker
import kotlin.math.abs
import kotlin.random.nextInt

fun Arb.Companion.int(min: Int, max: Int) = int(min..max)

/**
 * Returns an [Arb] where each value is a randomly chosen [Int] in the given range.
 * The edgecases are: [[Int.MIN_VALUE], [Int.MAX_VALUE], 0, 1, -1] with any not in the range
 * filtered out.
 */
fun Arb.Companion.int(range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE): Arb<Int> {
   val edgecases = listOf(0, 1, -1, Int.MAX_VALUE, Int.MIN_VALUE).filter { it in range }
   return arb(IntShrinker(range), edgecases) { it.random.nextInt(range) }
}

/**
 * Returns an [Arb] where each value is a randomly chosen natural integer.
 * The edge cases are: [[Int.MAX_VALUE], 1]
 */
fun Arb.Companion.nats(max: Int = Int.MAX_VALUE) = int(1..max).filter { it > 0 }

/**
 * Returns an [Arb] where each value is a randomly chosen negative integer.
 * The edge cases are: [[Int.MIN_VALUE], -1]
 */
fun Arb.Companion.negativeInts(min: Int = Int.MIN_VALUE) = int(min..0).filter { it < 0 }

/**
 * Returns an [Arb] where each value is a randomly chosen positive integer.
 * The edge cases are: [[Int.MAX_VALUE], 1]
 */
fun Arb.Companion.positiveInts(max: Int = Int.MAX_VALUE) = int(1..max).filter { it > 0 }

class IntShrinker(val range: IntRange) : Shrinker<Int> {
   override fun shrink(value: Int): List<Int> =
      when (value) {
         0 -> emptyList()
         1, -1 -> listOf(0).filter { it in range }
         else -> {
            val a = listOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct()
               .filterNot { it == value }
               .filter { it in range }
         }
      }
}

fun <A, B> Shrinker<A>.bimap(f: (B) -> A, g: (A) -> B): Shrinker<B> = object : Shrinker<B> {
   override fun shrink(value: B): List<B> = this@bimap.shrink(f(value)).map(g)
}
