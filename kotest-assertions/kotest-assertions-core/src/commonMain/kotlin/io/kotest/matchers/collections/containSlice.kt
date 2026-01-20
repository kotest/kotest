package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import kotlin.jvm.JvmName

/**
 * Assert that a collection contains the given slice.
 * For example,
 *  listOf(1, 1, 2) shouldContainSlice listOf(1, 2)    // Assertion passes
 *  listOf(1, 2, 1) shouldContainSlice listOf(1)       // Assertion passes
 *  listOf(1) shouldContainSlice listOf(1, 2)          // Assertion fails
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
@JvmName("shouldContainSlice_iterable")
infix fun <T> Iterable<T>.shouldContainSlice(expected: Iterable<T>) =
   this.toList() should containSlice(expected.toList())

fun <T> containSlice(slice: List<T>) = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      val contains = sliceStart(value, slice.toList()) != null
      val partialMatchesDescription = { describePartialMatchesInCollection(slice, value) }
//      val elementsToSearchForSimilar = partialMatchesDescription
      return MatcherResult(
         contains,
         { "List should contain slice ${slice.print().value} but was ${value.print().value}\n${partialMatchesDescription()}" },
         { "List should not contain slice ${slice.print().value}" }
      )
   }
}

internal fun<T> describePartialMatchesAndSimilarityInCollection(expectedSlice: Collection<T>, value: List<T>): SliceMatchDescription {
   val partialMatchesInCollectionDescription = describePartialMatchesInCollection(expectedSlice, value)
   return SliceMatchDescription(
      partialMatchesInCollectionDescription,
      similarElementsDescription = "",
   )
}

internal data class SliceMatchDescription(
   val partialMatchesInCollectionDescription: PartialMatchesInCollectionDescription,
   val similarElementsDescription: String,
)

internal fun<T> sliceStart(list: List<T>, slice: List<T>): Int? {
   if(list.size  < slice.size) return null
   (0 until list.size - slice.size + 1).forEach { index ->
      val match = slice.withIndex().all { (sliceIndex, sliceValue) ->
         list[index + sliceIndex] == sliceValue
      }
      if (match) return index
   }
   return null
}
