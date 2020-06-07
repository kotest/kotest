package io.kotest.property.arbitrary

import io.kotest.fp.firstOption
import io.kotest.property.Arb
import io.kotest.property.Shrinker
import kotlin.random.nextInt

/**
 * Returns an [Arb] where each random value is a String of length between minSize and maxSize.
 * By default the arb uses a [ascii] codepoint generator, but this can be substituted
 * with any codepoint generator. There are many available, such as [katakana] and so on.
 *
 * The edge case values are a string of min length, using the first
 * edgecase codepoint provided by the codepoints arb. If the min length is 0 and maxSize > 0, then
 * the edgecases will include a string of length 1 as well.
 */
fun Arb.Companion.string(
   minSize: Int = 0,
   maxSize: Int = 100,
   codepoints: Arb<Codepoint> = Arb.ascii()
): Arb<String> {

   val lowCodePoint = codepoints.edgecases().firstOption()
   val min = lowCodePoint.map { cp -> List(minSize) { cp.asString() }.joinToString("") }.orNull()
   val minPlus1 = lowCodePoint.map { cp -> List(minSize + 1) { cp.asString() }.joinToString("") }.orNull()

   val edgecases = listOfNotNull(min, minPlus1).filter { it.length >= minSize && it.length <= maxSize }

   return arb(StringShrinker, edgecases) { rs ->
      val codepointsIterator = codepoints.values(rs).iterator()
      val size = rs.random.nextInt(minSize..maxSize)
      List(size) { codepointsIterator.next().value }.joinToString("") { it.asString() }
   }
}

fun Arb.Companion.string(range: IntRange, codepoints: Arb<Codepoint> = Arb.ascii()): Arb<String> =
   Arb.string(range.first, range.last, codepoints)



object StringShrinker : Shrinker<String> {

   override fun shrink(value: String): List<String> {
      return when {
         value == "" -> emptyList()
         value == "a" -> listOf("")
         value.length == 1 -> listOf("", "a")
         else -> {
            val firstHalf = value.take(value.length / 2 + value.length % 2)
            val secondHalf = value.takeLast(value.length / 2)
            val secondHalfAs = firstHalf.padEnd(value.length, 'a')
            val firstHalfAs = secondHalf.padStart(value.length, 'a')
            val dropFirstChar = value.drop(1)
            val dropLastChar = value.dropLast(1)
            listOf(
               firstHalf,
               firstHalfAs,
               secondHalf,
               secondHalfAs,
               dropFirstChar,
               dropLastChar
            )
         }
      }
   }
}
