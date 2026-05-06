package io.kotest.matchers.string

import io.kotest.assertions.print.print
import io.kotest.assertions.similarity.possibleMatchesDescription
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResult.Companion.invoke

fun String.containExactCopies(
   element: String,
   copies: Int,
   allowOverlaps: Boolean,
   ) = object : Matcher<String> {
   override fun test(value: String) : MatcherResult {
      require(copies > 0) { "Copies should be positive, was $copies" }
      val passedAtIndexes = (0..<value.length - element.length).filter { index ->
            value.substring(index..<index + element.length) == element
      }
      val passed = passedAtIndexes.size == copies
      val possibleMatches = {
         if (!passed) {
            val candidates = possibleMatchesDescription(value.toSet(), element)
            if (candidates.isEmpty()) "" else "\nPossibleMatches:$candidates"
         } else ""
      }
      return MatcherResult(
         passed,
         {
            "String should contain $copies copies of element ${element.print().value}; " +
               "but contained ${passedAtIndexes.size} copies ${if(passedAtIndexes.size > 0) "at index(es) ${passedAtIndexes.print().value}, and " else "but "}" +
               "the collection is ${value.print().value}${possibleMatches()}"
         },
         { "Collection should not contain $copies copies of element ${element.print().value}, but it did at index(es):${passedAtIndexes.print().value}" }
      )
   }
}

internal fun removeOverlapsInIndexes(
   indexes: List<Int>,
   overlapLength: Int,
) : List<Int> {
   require(overlapLength > 0) { "Overlap length should be positive, was $overlapLength" }
   if (indexes.isEmpty()) return emptyList()
   return listOf(indexes[0]) + (1 until indexes.size).mapNotNull { index ->
      if(indexes[index - 1] != indexes[index] - overlapLength + 1) {
         indexes[index]
      } else {
         null
      }
   }.toList()
}
