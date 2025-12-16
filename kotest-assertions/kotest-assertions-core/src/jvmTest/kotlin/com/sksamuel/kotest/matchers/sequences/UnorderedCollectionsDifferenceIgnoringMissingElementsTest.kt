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
            expected = listOf("apple", "orange", "apple", "banana"),
            value = listOf("apple", "apple", "orange"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf(),
            extraElements = setOf(),
            countMismatches = listOf()
         )
            ).isMatch() shouldBe true
      }
      "detect extra elements" {
         UnorderedCollectionsDifference.matchIgnoringMissingElements(
            expected = listOf("apple", "orange", "apple"),
            value = listOf("apple", "apple", "orange", "banana"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf(),
            extraElements = setOf("banana"),
            countMismatches = listOf()
         )
      }
      "detect count mismatch if actual has extra values" {
         UnorderedCollectionsDifference.matchIgnoringMissingElements(
            expected = listOf("apple", "orange", "apple"),
            value = listOf("apple", "apple", "orange", "orange"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf(),
            extraElements = setOf(),
            countMismatches = listOf(
               UnorderedCollectionsDifference.CountMismatch(
                  value = "orange",
                  expectedCount = 1,
                  actualCount = 2
               )
            )
         )
      }
      "ignore count mismatch if actual has missing values" {
         UnorderedCollectionsDifference.matchIgnoringMissingElements(
            expected = listOf("apple", "orange", "orange", "apple"),
            value = listOf("apple", "apple", "orange"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf(),
            extraElements = setOf(),
            countMismatches = listOf()
         )
      }
   }
}
