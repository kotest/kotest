package io.kotest.property.arbitrary

import io.kotest.properties.nextPrintableString
import io.kotest.property.Shrinker
import io.kotest.property.Sample
import io.kotest.property.sampleOf
import kotlin.random.Random

/**
 * Returns an [Arb] where each random value is a String of length between minSize and maxSize.
 *
 * The edge cases values are:
 *
 * The empty string
 * A line separator
 * Multi-line string
 * a UTF8 string.
 */
fun Arb.Companion.string(
   minSize: Int = 0,
   maxSize: Int = 100
): Arb<String> = object : Arb<String> {

   private val range = minSize..maxSize

   val literals = listOf(
      "",
      "\n",
      "\nabc\n123\n",
      "\u006c\u0069b/\u0062\u002f\u006d\u0069nd/m\u0061x\u002e\u0070h\u0070"
   )

   override fun edgecases(): List<String> = literals.filter { it.length in range }
   override fun sample(random: Random): Sample<String> {
      val str = random.nextPrintableString(range.first + random.nextInt(range.last - range.first + 1))
      return sampleOf(str, StringShrinker)
   }
}

fun Arb.Companion.string(range: IntRange): Arb<String> = Arb.string(range.first, range.last)

object StringShrinker : Shrinker<String> {

   override fun shrink(value: String): List<String> {
      return when (value.length) {
         0 -> emptyList()
         1 -> listOf("", "a")
         else -> {
            val first = value.take(value.length / 2 + value.length % 2)
            val second = value.takeLast(value.length / 2)
            listOf(first, first.padEnd(value.length, 'a'), second, second.padStart(value.length, 'a'))
         }
      }
   }
}
