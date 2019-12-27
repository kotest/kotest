package io.kotest.property.arbitrary

import io.kotest.properties.nextPrintableString
import io.kotest.property.Arbitrary
import io.kotest.property.PropertyInput
import io.kotest.property.Shrinker
import kotlin.random.Random

/**
 * Returns an [Arbitrary] where each random value is a String.
 * The edge cases values are:
 *
 * The empty string
 * A line separator
 * Multi-line string
 * a UTF8 string.
 */
fun Arbitrary.Companion.string(
   iterations: Int = 100,
   minSize: Int = 0,
   maxSize: Int = 100
): Arbitrary<String> = object : Arbitrary<String> {

   val range = minSize..maxSize

   val literals = listOf(
      "",
      "\n",
      "\nabc\n123\n",
      "\u006c\u0069b/\u0062\u002f\u006d\u0069nd/m\u0061x\u002e\u0070h\u0070"
   )

   override fun edgecases(): Iterable<String> = literals.filter { it.length in range }

   override fun samples(random: Random): Sequence<PropertyInput<String>> {
      return generateSequence {
         random.nextPrintableString(range.first + random.nextInt(range.last - range.first + 1))
      }.map { PropertyInput(it, StringShrinker) }.take(iterations)
   }
}


object StringShrinker : Shrinker<String> {
   override fun shrink(value: String): List<PropertyInput<String>> = when (value.length) {
      0 -> emptyList()
      1 -> listOf(PropertyInput(""), PropertyInput("a"))
      else -> {
         val first = value.take(value.length / 2 + value.length % 2)
         val second = value.takeLast(value.length / 2)
         // always include empty string as the best io.kotest.properties.shrinking.shrink
         listOf("", first, first.padEnd(value.length, 'a'), second, second.padStart(value.length, 'a'))
            .map { PropertyInput(it, StringShrinker) }
      }
   }
}
