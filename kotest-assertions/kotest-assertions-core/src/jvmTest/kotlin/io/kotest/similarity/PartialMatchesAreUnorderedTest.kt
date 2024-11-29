package io.kotest.similarity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.submatching.MatchedCollectionElement
import io.kotest.submatching.PartialCollectionMatch

class PartialMatchesAreUnorderedTest: StringSpec() {
   init {
      "false for empty list" {
         partialMatchesAreUnordered(emptyList()) shouldBe false
      }
      "false for list of one element" {
         partialMatchesAreUnordered(listOf(
            PartialCollectionMatch(
               MatchedCollectionElement(1, 2),
               length = 10
            )
         )) shouldBe false
      }
      "false for ordered list" {
         partialMatchesAreUnordered(listOf(
            PartialCollectionMatch(
               MatchedCollectionElement(1, 2),
               length = 10
            ),
            PartialCollectionMatch(
               MatchedCollectionElement(20, 20),
               length = 10
            ),
         )) shouldBe false
      }
      "true for unordered list" {
         partialMatchesAreUnordered(listOf(
            PartialCollectionMatch(
               MatchedCollectionElement(1, 20),
               length = 10
            ),
            PartialCollectionMatch(
               MatchedCollectionElement(20, 2),
               length = 10
            ),
         )) shouldBe true
      }
   }
}

