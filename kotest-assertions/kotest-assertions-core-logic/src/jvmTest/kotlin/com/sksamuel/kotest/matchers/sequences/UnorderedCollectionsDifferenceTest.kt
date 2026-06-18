package com.sksamuel.kotest.matchers.sequences

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.sequences.UnorderedCollectionsDifference
import io.kotest.matchers.shouldBe

class UnorderedCollectionsDifferenceTest: StringSpec() {
   init {
      "detect match" {
         UnorderedCollectionsDifference.of(
            expected = listOf("apple", "orange", "apple"),
            value = listOf("apple", "apple", "orange"),
         ).isMatch() shouldBe true
      }
      "detect missing elements" {
         UnorderedCollectionsDifference.of(
            expected = listOf("apple", "orange", "apple", "banana"),
            value = listOf("apple", "apple", "orange"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf("banana"),
            extraElements = setOf(),
            countMismatches = listOf()
         )
      }
      "detect extra elements" {
         UnorderedCollectionsDifference.of(
            expected = listOf("apple", "orange", "apple"),
            value = listOf("apple", "apple", "orange", "banana"),
         ) shouldBe UnorderedCollectionsDifference(
            missingElements = setOf(),
            extraElements = setOf("banana"),
            countMismatches = listOf()
         )
      }
      "detect count mismatch" {
         UnorderedCollectionsDifference.of(
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
   }
}
