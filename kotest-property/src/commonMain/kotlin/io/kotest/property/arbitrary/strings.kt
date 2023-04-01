package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.strings.StringClassifier
import kotlin.random.nextInt

/**
 * Returns an [Arb] where each random value is a String of length between minSize and maxSize.
 * By default, the arb uses a [ascii] codepoint generator, but this can be substituted
 * with any codepoint generator. There are many available, such as [katakana] and so on.
 *
 * The edge case values are a string of min length, using the first
 * edge case codepoint provided by the codepoints arb. If the min length is 0 and maxSize > 0, then
 * the edge cases will include a string of length 1 as well.
 */
fun Arb.Companion.string(
   minSize: Int = 0,
   maxSize: Int = 100,
   codepoints: Arb<Codepoint> = Codepoint.ascii()
): Arb<String> {

   return ArbitraryBuilder.create { rs ->
      val size = rs.random.nextInt(minSize..maxSize)
      codepoints.take(size, rs).joinToString("") { it.asString() }
   }.withEdgecaseFn { rs ->
      if (minSize == maxSize) null else {
         val lowCodePoint = codepoints.edgecase(rs)
         val min = lowCodePoint?.let { cp -> List(minSize) { cp.asString() }.joinToString("") }
         val minPlus1 = lowCodePoint?.let { cp -> List(minSize + 1) { cp.asString() }.joinToString("") }
         val edgeCases = listOfNotNull(min, minPlus1)
            .filter { it.length in minSize..maxSize }
         if (edgeCases.isEmpty()) null else edgeCases.random(rs.random)
      }
   }.withShrinker(StringShrinkerWithMin(minSize))
      .withClassifier(StringClassifier(minSize, maxSize))
      .build()
}

/**
 * Returns an [Arb] where each random value is a String which has a length in the given range.
 * By default the arb uses a [ascii] codepoint generator, but this can be substituted
 * with any codepoint generator. There are many available, such as [katakana] and so on.
 *
 * The edge case values are a string of the first value in the range, using the first edge case
 * codepoint provided by the codepoints arb.
 */
fun Arb.Companion.string(range: IntRange, codepoints: Arb<Codepoint> = Codepoint.ascii()): Arb<String> =
   Arb.string(range.first, range.last, codepoints)

/**
 * Returns an [Arb] where each random value is a String of length [size].
 * By default the arb uses a [ascii] codepoint generator, but this can be substituted
 * with any codepoint generator. There are many available, such as [katakana] and so on.
 *
 * There are no edge case values associated with this arb.
 */
fun Arb.Companion.string(size: Int, codepoints: Arb<Codepoint> = Codepoint.ascii()): Arb<String> =
   Arb.string(size, size, codepoints)

@Deprecated("This Shrinker does not take into account string lengths. Use StringShrinkerWithMin. This was deprecated in 4.5.")
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

/**
 * Shrinks a string. Shrunk variants will be shorter and simplified.
 *
 * Shorter strings will be at least [minLength] in length.
 *
 * Simplified strings will have characters replaced by a character (selected by [simplestCharSelector])
 * of each pre-shrunk value. By default, this is the first character of the pre-shrunk string.
 *
 * When [simplestCharSelector] returns null, no simpler variants will be created.
 */
class StringShrinkerWithMin(
   private val minLength: Int = 0,
   private val simplestCharSelector: (preShrinkValue: String) -> Char? = CharSequence::firstOrNull
) : Shrinker<String> {

   override fun shrink(value: String): List<String> {

      val simplestChar: Char? = simplestCharSelector(value)

      val isShortest = value.length == minLength
      val isSimplest = value.all { it == simplestChar }

      return buildList {
         if (!isShortest) {
            addAll(shorterVariants(value))
         }
         if (!isSimplest && simplestChar != null) {
            addAll(simplerVariants(value, simplestChar))
         }
      }.mapNotNull {
         // ensure the variants are at least minLength long
         when {
            simplestChar != null -> it.padEnd(minLength, simplestChar)
            it.length >= minLength -> it
            else -> null // this string is too short, so filter it out
         }
      }.distinct()
   }

   private fun simplerVariants(value: String, simplestChar: Char): List<String> =
      listOfNotNull(
         // replace the first and last chars that aren't simplestChar with simplestChar
         replace(value, simplestChar, value.indexOfFirst { it != simplestChar }),
         replace(value, simplestChar, value.indexOfLast { it != simplestChar }),
      )

   private fun shorterVariants(value: String) =
      listOf(
         value.take(value.length / 2 + value.length % 2),
         value.takeLast(value.length / 2),
         value.drop(1),
         value.dropLast(1),
      )

   private fun replace(value: String, newChar: Char, index: Int) =
      if (index == -1) null else value.replaceRange(index..index, newChar.toString())
}
