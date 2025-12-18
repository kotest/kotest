package io.kotest.matchers.sequences

import io.kotest.assertions.print.print

internal data class UnorderedCollectionsDifference<T>(
   val missingElements: Set<T>,
   val extraElements: Set<T>,
   val countMismatches: List<CountMismatch<T>>
) {
   private val describedMismatches = sequenceOf(
      DescribedMismatch(missingElements, "Missing Elements"),
      DescribedMismatch(extraElements, "Extra Elements"),
      DescribedMismatch(countMismatches, "Count Mismatches")
   )

   fun isMatch(): Boolean {
      return describedMismatches.all { it.elements.isEmpty() }
   }

   override fun toString(): String {
      return if (isMatch()) "" else "\n" + describedMismatches
         .map { it.toString() }
         .filter { it.isNotEmpty() }
         .joinToString("\n")
   }

   private data class DescribedMismatch<T>(
      val elements: Collection<T>,
      val description: String
   ) {
      override fun toString(): String {
         return if (elements.isEmpty()) "" else
             "$description:\n${elements.joinToString("\n") { it.print().value }}"
      }
   }

   companion object {
      fun<T> of(expected: List<T>, value: List<T>): UnorderedCollectionsDifference<T> {
         val expectedCounts = expected.counted()
         val valueCounts = value.counted()
         return UnorderedCollectionsDifference(
             missingElements = expectedCounts.keys - valueCounts.keys,
             extraElements = valueCounts.keys - expectedCounts.keys,
             countMismatches = expectedCounts.mapNotNull { ex ->
                 val valueCount = valueCounts[ex.key]
                 valueCount?.let {
                     if (ex.value == valueCount) null else
                         CountMismatch(ex.key, ex.value, valueCount)
                 }
             }
         )
      }

      fun<T> matchIgnoringMissingElements(expected: List<T>, value: List<T>): UnorderedCollectionsDifference<T> {
         val expectedCounts = expected.counted()
         val valueCounts = value.counted()
         return UnorderedCollectionsDifference(
            missingElements = expectedCounts.keys - valueCounts.keys,
            extraElements = setOf(),
            countMismatches = expectedCounts.mapNotNull { ex ->
               val valueCount = valueCounts[ex.key]
               valueCount?.let {
                  if (ex.value <= valueCount) null else
                     CountMismatch(ex.key, ex.value, valueCount)
               }
            }
         )
      }
   }

   internal data class CountMismatch<T>(
      val value: T,
      val expectedCount: Int,
      val actualCount: Int
   ) {
      init {
         require(expectedCount != actualCount) { "Expected count should be different from actual, but both were: $expectedCount" }
      }

      override fun toString(): String = "  For ${value.print().value}: expected count: <$expectedCount>, but was: <$actualCount>"
   }
}
