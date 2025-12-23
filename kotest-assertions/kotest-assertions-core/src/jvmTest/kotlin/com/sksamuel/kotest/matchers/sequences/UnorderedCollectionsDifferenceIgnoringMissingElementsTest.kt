package com.sksamuel.kotest.matchers.sequences

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.sequences.UnorderedCollectionsDifference
import io.kotest.matchers.shouldBe

class UnorderedCollectionsDifferenceIgnoringMissingElementsTest: StringSpec() {
   init {
      "detect match" {
         UnorderedCollectionsDifference.matchIgnoringMissingElements(
            expected = listOf("apple", "orange", "apple"),
            value = listOf("apple", "apple", "orange"),
         ).isMatch() shouldBe true
      }
      "ignore missing elements" {
         (UnorderedCollectionsDifference.matchIgnoringMissingElements(
            value = listOf("apple", "orange", "apple", "banana"),
            expected = listOf("apple", "apple", "orange"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf(),
            extraElements = setOf(),
            countMismatches = listOf()
         )
            ).isMatch() shouldBe true
      }
      "detect extra elements" {
         UnorderedCollectionsDifference.matchIgnoringMissingElements(
            value = listOf("apple", "orange", "apple"),
            expected = listOf("apple", "apple", "orange", "banana"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf("banana"),
            extraElements = setOf(),
            countMismatches = listOf()
         )
      }
      "detect count mismatch if actual has extra values" {
         UnorderedCollectionsDifference.matchIgnoringMissingElements(
            value = listOf("apple", "orange", "apple"),
            expected = listOf("apple", "apple", "orange", "orange"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf(),
            extraElements = setOf(),
            countMismatches = listOf(
               UnorderedCollectionsDifference.CountMismatch(
                  value = "orange",
                  expectedCount = 2,
                  actualCount = 1
               )
            )
         )
      }
      "ignore count mismatch if actual has missing values" {
         UnorderedCollectionsDifference.matchIgnoringMissingElements(
            value = listOf("apple", "orange", "orange", "apple"),
            expected = listOf("apple", "apple", "orange"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf(),
            extraElements = setOf(),
            countMismatches = listOf()
         )
      }
   }
}
