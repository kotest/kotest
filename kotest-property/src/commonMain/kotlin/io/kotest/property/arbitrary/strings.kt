package io.kotest.property.arbitrary

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

   return arbitrary(
      edgecaseFn = { rs ->
         if (minSize == maxSize) null else {
            val lowCodePoint = codepoints.edgecase(rs)
            val min = lowCodePoint?.let { cp -> List(minSize) { cp.asString() }.joinToString("") }
            val minPlus1 = lowCodePoint?.let { cp -> List(minSize + 1) { cp.asString() }.joinToString("") }
            val edgecases = listOfNotNull(min, minPlus1)
               .filter { it.length in minSize..maxSize }
            if (edgecases.isEmpty()) null else edgecases.random(rs.random)
         }
      },
      shrinker = StringShrinkerWithMin(minSize),
      sampleFn = { rs ->
         val size = rs.random.nextInt(minSize..maxSize)
         codepoints.take(size, rs).joinToString("") { it.asString() }
      }
   )
}

/**
 * Returns an [Arb] where each random value is a String which has a length in the given range.
 * By default the arb uses a [ascii] codepoint generator, but this can be substituted
 * with any codepoint generator. There are many available, such as [katakana] and so on.
 *
 * The edge case values are a string of the first value in the range, using the first edgecase
 * codepoint provided by the codepoints arb.
 */
fun Arb.Companion.string(range: IntRange, codepoints: Arb<Codepoint> = Arb.ascii()): Arb<String> =
   Arb.string(range.first, range.last, codepoints)

/**
 * Returns an [Arb] where each random value is a String of length [size].
 * By default the arb uses a [ascii] codepoint generator, but this can be substituted
 * with any codepoint generator. There are many available, such as [katakana] and so on.
 *
 * There are no edge case values associated with this arb.
 */
fun Arb.Companion.string(size: Int, codepoints: Arb<Codepoint> = Arb.ascii()): Arb<String> =
   Arb.string(size, size, codepoints)

@Deprecated("use StringShrinkerWithMin. This will be removed in 4.7")
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

class StringShrinkerWithMin(
   private val minLength: Int = 0,
   private val simplestChar: Char = 'a',
) : Shrinker<String> {

   override fun shrink(value: String): List<String> {

      val isShortest = value.length == minLength
      val isSimplest = value.all { it == simplestChar }

      return when {
         isShortest && isSimplest -> emptyList()
         isShortest -> simplerVariants(value)
         isSimplest -> shorterVariants(value)
         else -> shorterVariants(value) + simplerVariants(value)
      }.map { it.padEnd(minLength, simplestChar) }.distinct()
   }

   private fun simplerVariants(value: String) =
      listOfNotNull(replaceFirst(value, simplestChar), replaceLast(value, simplestChar))

   private fun shorterVariants(value: String) =
      listOf(
         value.take(value.length / 2 + value.length % 2),
         value.takeLast(value.length / 2),
         value.drop(1),
         value.dropLast(1),
      )

   private fun replaceFirst(value: String, newChar: Char): String? =
      replace(value, newChar, value.indexOfFirst { it != simplestChar })

   private fun replaceLast(value: String, newChar: Char): String? =
      replace(value, newChar, value.indexOfLast { it != simplestChar })

   private fun replace(value: String, newChar: Char, index: Int) =
      if (index == -1) null else value.replaceRange(index..index, newChar.toString())
}
