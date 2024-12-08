package io.kotest.matchers.collections

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.submatching.MatchedCollectionElement
import io.kotest.submatching.PartialCollectionMatch

class DescribePartialMatchesInCollectionTest: WordSpec() {
   init {
       "describePartialMatchesInCollection" should {
          "work when one element not found" {
             val actual = describePartialMatchesInCollection(
                 expectedSlice = listOf("orange", "apple", "banana", "blueberry"),
                 value = listOf("apple", "banana", "blueberry")
             )
             actual.unmatchedElementsDescription shouldBe ""
             actual.partialMatches shouldContainExactly listOf(
                PartialCollectionMatch(
                   matchedElement= MatchedCollectionElement(
                      startIndexInExpected=1,
                      startIndexInValue=0
                   ),
                   length=3
                )
             )
          }
          "work when one element found in another place" {
             val actual = describePartialMatchesInCollection(
                expectedSlice = listOf("orange", "apple", "banana", "blueberry"),
                value = listOf("apple", "banana", "blueberry", "orange")
             )
             actual.unmatchedElementsDescription shouldBe """[0] "orange" => Found At Index(es): [3]"""
             actual.partialMatches shouldContainExactly listOf(
                PartialCollectionMatch(
                   matchedElement = MatchedCollectionElement(
                      startIndexInExpected = 1,
                      startIndexInValue = 0
                   ), length = 3
                )
             )
          }
          "work when one element found in multiple places" {
             val actual = describePartialMatchesInCollection(
                expectedSlice = listOf("orange", "apple", "banana", "blueberry"),
                value = listOf("apple", "banana", "blueberry", "orange", "lemon", "orange")
             )
             actual.unmatchedElementsDescription shouldBe """[0] "orange" => Found At Index(es): [3, 5]"""
             actual.partialMatches shouldContainExactly listOf(
                PartialCollectionMatch(
                   matchedElement = MatchedCollectionElement(
                      startIndexInExpected = 1,
                      startIndexInValue = 0
                   ), length = 3
                )
             )
          }
          "work when multiple elements found" {
             val actual = describePartialMatchesInCollection(
                expectedSlice = listOf("orange", "apple", "banana", "blueberry", "lemon"),
                value = listOf("apple", "banana", "blueberry", "orange", "lemon", "orange")
             )
             actual.unmatchedElementsDescription.shouldContainInOrder(
                """[0] "orange" => Found At Index(es): [3, 5]""",
                """[4] "lemon" => Found At Index(es): [4]""",
             )
             actual.partialMatches shouldContainExactly listOf(
                PartialCollectionMatch(
                   matchedElement = MatchedCollectionElement(
                      startIndexInExpected = 1,
                      startIndexInValue = 0
                   ), length = 3
                )
             )
          }
          "work for complete match" {
             val expectedSlice = listOf("orange", "apple", "banana", "cherry")
             val actual = describePartialMatchesInCollection(
                expectedSlice = expectedSlice,
                value = expectedSlice
             )
             actual.unmatchedElementsDescription shouldBe ""
          }
       }

   }
}
