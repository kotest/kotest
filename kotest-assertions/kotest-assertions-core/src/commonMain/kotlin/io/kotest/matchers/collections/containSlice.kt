package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.assertions.similarity.possibleMatchesDescription
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
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

/**
 * Assert that a collection contains the given slice.
 * For example,
 *  listOf(1, 1, 2) shouldContainNotSlice listOf(1, 2)    // Assertion fails
 *  listOf(1, 2, 1) shouldContainNotSlice listOf(1)       // Assertion fails
 *  listOf(1) shouldContainNotSlice listOf(1, 2)          // Assertion passes
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
@JvmName("shouldNotContainSlice_iterable")
infix fun <T> Iterable<T>.shouldNotContainSlice(expected: Iterable<T>) =
   this.toList() shouldNot containSlice(expected.toList())

/**
 * Assert that a collection contains the given slice.
 * For example,
 *  arrayOf(1, 1, 2) shouldContainSlice listOf(1, 2)    // Assertion passes
 *  arrayOf(1, 2, 1) shouldContainSlice listOf(1)       // Assertion passes
 *  arrayOf(1) shouldContainSlice listOf(1, 2)          // Assertion fails
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
@JvmName("shouldContainSlice_array")
infix fun <T> Array<T>.shouldContainSlice(expected: Iterable<T>) =
   this.toList() should containSlice(expected.toList())

/**
 * Assert that a collection contains the given slice.
 * For example,
 *  arrayOf(1, 1, 2) shouldContainNotSlice listOf(1, 2)    // Assertion fails
 *  arrayOf(1, 2, 1) shouldContainNotSlice listOf(1)       // Assertion fails
 *  arrayOf(1) shouldContainNotSlice listOf(1, 2)          // Assertion passes
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
@JvmName("shouldNotContainSlice_array")
infix fun <T> Array<T>.shouldNotContainSlice(expected: Iterable<T>) =
   this.toList() shouldNot containSlice(expected.toList())

fun <T> containSlice(slice: List<T>) = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      val contains = sliceStart(value, slice.toList()) != null
      return MatcherResult(
         contains,
         { "List should contain slice ${slice.print().value} but was ${value.print().value}\n${
            describePartialMatchesAndSimilarityInCollection(expectedSlice = slice, value = value)
         }" },
         { "List should not contain slice ${slice.print().value}" }
      )
   }
}

internal fun<T> describePartialMatchesAndSimilarityInCollection(expectedSlice: Collection<T>, value: List<T>): String {
   val partialMatchesInCollectionDescription = describePartialMatchesInCollection(expectedSlice, value)
   val expectedSliceAsList = expectedSlice.toList()
   val unmatchedElements = partialMatchesInCollectionDescription.indexesOfUnmatchedElements.map {
      it to expectedSliceAsList[it]
   }
   val elementsFoundElsewhere = unmatchedElements.mapNotNull { (index, element) ->
      val foundAtIndex = value.indexOf(element)
      if(foundAtIndex == -1) null else index to foundAtIndex
   }
   val elementsToSearchForSimilarity = unmatchedElements.filter { (index, _) ->
      elementsFoundElsewhere.none { (foundIndex, _) -> foundIndex == index }
   }
   val similarElements = elementsToSearchForSimilarity.map {
      it.first to possibleMatchesDescription(value.toSet(), it.second)
   }.filter { it.second.isNotEmpty() }
   return listOf(
      partialMatchesInCollectionDescription.toString(),
      similarElements.takeIf { it.isNotEmpty() }?.joinToString(
         separator = "\n",
         prefix = "Found similar elements for elements not in matched slice(s):\n",
         ) { (index, description) ->
         "[$index] ${expectedSliceAsList[index].print().value} has similar element(s): $description"
      } ?: "",
   )
      .filter { it.isNotEmpty() }
      .joinToString("\n")
}

internal data class SliceMatchDescription(
   val partialMatchesInCollectionDescription: PartialMatchesInCollectionDescription,
   val exactMatchesDescription: String,
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
