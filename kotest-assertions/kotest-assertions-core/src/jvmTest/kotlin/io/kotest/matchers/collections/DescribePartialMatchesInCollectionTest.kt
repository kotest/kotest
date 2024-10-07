package io.kotest.matchers.collections

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class DescribePartialMatchesInCollectionTest: WordSpec() {
   init {
       "describePartialMatchesInCollection" should {
          "find indexes of mismatches" {
             val actual = describePartialMatchesInCollection(
                 expectedSlice = listOf("orange", "apple", "banana", "cherry"),
                 value = listOf("apple", "banana", "blueberry")
             )
             actual.indexesOfUnmatchedElements shouldContainExactlyInAnyOrder listOf(0, 3)
          }
          "return empty list for complete match" {
             val expectedSlice = listOf("orange", "apple", "banana", "cherry")
             val actual = describePartialMatchesInCollection(
                expectedSlice = expectedSlice,
                value = expectedSlice
             )
             actual.indexesOfUnmatchedElements.shouldBeEmpty()
          }
       }

   }
}
