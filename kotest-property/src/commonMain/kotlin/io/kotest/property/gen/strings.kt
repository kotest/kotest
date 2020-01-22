package io.kotest.property.gen

import io.kotest.properties.nextPrintableString
import io.kotest.property.Gen
import io.kotest.property.Shrinker
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Returns a [Gen] where each random value is a String.
 * The edge cases values are:
 *
 * The empty string
 * A line separator
 * Multi-line string
 * a UTF8 string.
 */
fun Gen.Companion.string(sizes: IntRange = 0..100): Gen<String> = object : Gen<String> {

   val literals = listOf(
      "",
      "\n",
      "\nabc\n123\n",
      "\u006c\u0069b/\u0062\u002f\u006d\u0069nd/m\u0061x\u002e\u0070h\u0070"
   )

   override fun edgecases(): Iterable<String> = literals.filter { it.length in sizes }

   override fun shrinker(): Shrinker<String>? = StringShrinker

   override fun generate(random: Random): String {
      return random.nextPrintableString(random.nextInt(sizes))
   }
}

object StringShrinker : Shrinker<String> {
   override fun shrink(value: String): List<String> = when (value.length) {
      0 -> emptyList()
      1 -> listOf("", "a")
      else -> {
         val first = value.take(value.length / 2 + value.length % 2)
         val second = value.takeLast(value.length / 2)
         listOf(first, first.padEnd(value.length, 'a'), second, second.padStart(value.length, 'a'))
      }
   }
}
