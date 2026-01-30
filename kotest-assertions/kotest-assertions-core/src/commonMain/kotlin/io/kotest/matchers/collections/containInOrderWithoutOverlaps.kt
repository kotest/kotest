package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.assertions.submatching.describePartialMatchesInStringForSlice
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.ContainInOrderOutcome
import io.kotest.matchers.string.joinNonEmpty
import io.kotest.matchers.string.matchSubstrings


/**
 * Verifies that the given [String] contains all the specified substrings in given order,
 * with any characters before, after, or in between.
 *
 * For example, each of the following examples would pass:
 *
 * val value = "The quick brown fox jumps over the lazy dog"
 * value.shouldContainInOrderWithoutOverlaps("The", "quick", "fox", "jumps", "over", "dog")
 * value.shouldContainInOrderWithoutOverlaps("The quick", "fox jump", "over", "dog")
 * "superstar".shouldContainInOrderWithoutOverlaps("super", "star")
 *
 * Note: unlike [shouldContainInOrder], consecutive substrings cannot overlap.
 * So the following tests would fail:
 *
 * "sourdough bread".shouldContainInOrderWithoutOverlaps("bread", "read")
 * "superstar".shouldContainInOrderWithoutOverlaps("supers", "star")
 */
fun String?.shouldContainInOrderWithoutOverlaps(vararg substrings: String): String? {
   this should containInOrderWithoutOverlaps(*substrings)
   return this
}

fun String?.shouldNotContainInOrderWithoutOverlaps(vararg substrings: String): String? {
   this shouldNot containInOrderWithoutOverlaps(*substrings)
   return this
}

fun containInOrderWithoutOverlaps(vararg substrings: String) = neverNullMatcher<String> { value ->
   val matchOutcome = matchSubstrings(value, substrings.toList())

   val substringFoundEarlier = if (matchOutcome is ContainInOrderOutcome.Mismatch) {
      describePartialMatchesInStringForSlice(matchOutcome.substring, value).toString()
   } else ""

   val completeMismatchDescription = joinNonEmpty(
      "\n",
      matchOutcome.mistmatchDescription,
      substringFoundEarlier
   )

   MatcherResult(
      matchOutcome.match,
      {
         "${value.print().value} should include substrings ${substrings.print().value} in order${
            io.kotest.matchers.string.prefixIfNotEmpty(
               completeMismatchDescription,
               "\n"
            )
         }"
      },
      { "${value.print().value} should not include substrings ${substrings.print().value} in order" })
}

internal fun matchSubstringsWithoutOverlaps(value: String, substrings: List<String>, depth: Int = 0): ContainInOrderOutcome = when {
   substrings.isEmpty() -> ContainInOrderOutcome.Match
   else -> {
      val currentSubstring = substrings[0]
      val matchAtIndex = value.indexOf(currentSubstring)
      when {
         matchAtIndex == -1 -> ContainInOrderOutcome.Mismatch(currentSubstring, depth)
         currentSubstring == "" -> matchSubstrings(value, substrings.drop(1), depth + currentSubstring.length)
         else -> matchSubstrings(value.substring(matchAtIndex + 1), substrings.drop(1), depth + currentSubstring.length)
      }
   }
}
