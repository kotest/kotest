package io.kotest.matchers.string

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Verifies that the given [String] contains the specified substring
 * exactly N times, with or without overlaps
 * with any characters before, after, or in between.
 *
 * For example, each of the following assertions would pass:
 *
 * "Mayday".shouldContainExactCopies("ay", copies = 2, allowOverlaps = false)
 * "121212".shouldContainExactCopies("1212", copies = 2, allowOverlaps = true)
 *
 * and the following assertions will fail:
 *
 * "Mayday".shouldContainExactCopies("ay", copies = 1, allowOverlaps = false)
 * "Mayday".shouldContainExactCopies("ay", copies = 3, allowOverlaps = false)
 * "121212".shouldContainExactCopies("1212", copies = 2, allowOverlaps = false)
 */
fun String.shouldContainExactCopies(
   element: String,
   copies: Int,
   allowOverlaps: Boolean
) : String {
   this should containExactCopies(element, copies, allowOverlaps)
   return this
}

/**
 * Verifies that the given [String] does not contain the specified substring
 * exactly N times, with or without overlaps
 * with any characters before, after, or in between.
 *
 * For example, each of the following assertions would pass:
 *
 * "Mayday".shouldNotContainExactCopies("ay", copies = 1, allowOverlaps = false)
 * "Mayday".shouldNotContainExactCopies("ay", copies = 3, allowOverlaps = false)
 * "121212".shouldNotContainExactCopies("1212", copies = 2, allowOverlaps = false)
 *
 * and the following assertions will fail:
 *
 * "Mayday".shouldNotContainExactCopies("ay", copies = 2, allowOverlaps = false)
 * "121212".shouldNotContainExactCopies("1212", copies = 2, allowOverlaps = true)
 *
 */
fun String.shouldNotContainExactCopies(
   element: String,
   copies: Int,
   allowOverlaps: Boolean
) : String {
   this shouldNot containExactCopies(element, copies, allowOverlaps)
   return this
}

fun String.containExactCopies(
   substring: String,
   copies: Int,
   allowOverlaps: Boolean,
   ) = object : Matcher<String> {
   override fun test(value: String) : MatcherResult {
      require(substring.isNotEmpty()) { "Element should not be empty" }
      require(copies > 0) { "Copies should be positive, was $copies" }
      val containsAtIndexes = substringFoundAtIndexes(
         value,
         substring,
      )
      val passedAtIndexes = if(allowOverlaps) {
         containsAtIndexes
      } else {
         removeOverlapsInIndexes(containsAtIndexes, substring.length)
      }
      val passed = passedAtIndexes.size == copies
      return MatcherResult(
         passed,
         {
            "String should contain $copies copies of element ${substring.print().value}; " +
               "but contained ${passedAtIndexes.size} copies ${if(passedAtIndexes.size > 0) "at index(es) ${passedAtIndexes.print().value}, and " else "but "}" +
               "the collection is ${value.print().value}"
         },
         { "String should not contain $copies copies of element ${substring.print().value}, but it did at index(es):${passedAtIndexes.print().value}" }
      )
   }
}

internal fun substringFoundAtIndexes(
   value: String,
   substring: String,
) : List<Int> {
   require(substring.isNotEmpty()) { "Substring should not be empty" }
   return (0..value.length - substring.length).filter { index ->
      value.substring(index..<index + substring.length) == substring
   }
}

internal fun removeOverlapsInIndexes(
   indexes: List<Int>,
   overlapLength: Int,
) : List<Int> {
   require(overlapLength > 0) { "Overlap length should be positive, was $overlapLength" }
   if (indexes.isEmpty()) return emptyList()
   val nonOverlappingIndexes = mutableListOf(indexes[0])
   (1 until indexes.size).forEach { index ->
      if(indexes[index] >= nonOverlappingIndexes.last() + overlapLength) {
         nonOverlappingIndexes.add(indexes[index])
      }
   }
   return nonOverlappingIndexes.toList()
}
