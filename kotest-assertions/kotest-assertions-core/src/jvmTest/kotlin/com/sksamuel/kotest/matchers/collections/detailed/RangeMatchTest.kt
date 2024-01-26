package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.ItemsMatch
import io.kotest.matchers.collections.detailed.MatchResultType
import io.kotest.matchers.collections.detailed.MatchResultsOfSubLists

class RangeMatchTest: StringSpec() {
   init {
      "extendLeftRangeBack" {
         val sut = MatchResultsOfSubLists(false, 1..2, 2..3)
         val actual = sut.extendLeftRangeBack()
         val expected = MatchResultsOfSubLists(false, 0..2, 2..3)
         actual shouldBe expected
      }

      "extendRightRangeBack" {
         val sut = MatchResultsOfSubLists(false, 1..2, 2..3)
         val actual = sut.extendRightRangeBack()
         val expected = MatchResultsOfSubLists(false, 1..2, 1..3)
         actual shouldBe expected
      }

      "extendBothRangesBack" {
         val sut = MatchResultsOfSubLists(true, 1..2, 2..3)
         val actual = sut.extendBothRangesBack()
         val expected = MatchResultsOfSubLists(true, 0..2, 1..3)
         actual shouldBe expected
      }

      "spawnNewRange_mismatchLEFT" {
         val sut = MatchResultsOfSubLists(true, 1..2, 2..3)
         val actual = sut.spawnNewRange(ItemsMatch(false, MatchResultType.LEFT_ELEMENT_ONLY))
         val expected = MatchResultsOfSubLists(false, 0..0, 1..0)
         actual shouldBe expected
      }

      "spawnNewRange_mismatchRIGHT" {
         val sut = MatchResultsOfSubLists(true, 1..2, 2..3)
         val actual = sut.spawnNewRange(ItemsMatch(false, MatchResultType.RIGHT_ELEMENT_ONLY))
         val expected = MatchResultsOfSubLists(false, 0..-1, 1..1)
         actual shouldBe expected
      }

      "spawnNewRange_mismatchBOTH" {
         val sut = MatchResultsOfSubLists(true, 1..2, 2..3)
         val actual = sut.spawnNewRange(ItemsMatch(false, MatchResultType.BOTH_ELEMENTS_PRESENT))
         val expected = MatchResultsOfSubLists(false, 0..0, 1..1)
         actual shouldBe expected
      }

      "spawnNewRange_matchBOTH" {
         val sut = MatchResultsOfSubLists(false, 1..2, 2..3)
         val actual = sut.spawnNewRange(ItemsMatch(true, MatchResultType.BOTH_ELEMENTS_PRESENT))
         val expected = MatchResultsOfSubLists(true, 0..0, 1..1)
         actual shouldBe expected
      }

      "rightIndexes_WorksForSeveralIndexes" {
         val sut = MatchResultsOfSubLists(false, 1..2, 2..4)
         sut.rightIndexes shouldBe listOf(2, 3, 4)
      }

      "rightIndexes_WorksForOneIndex" {
         val sut = MatchResultsOfSubLists(false, 1..2, 2..2)
         sut.rightIndexes shouldBe listOf(2)
      }

      "init_ifMatchRangesHaveSameLength" {
         shouldThrow<IllegalArgumentException> {
            MatchResultsOfSubLists(true, 1..2, 3..5)
         }
      }

      "init_ifLeftRangeHasIncorrectLength" {
         shouldThrow<IllegalArgumentException> {
            MatchResultsOfSubLists(false, 1..-1, 3..5)
         }
      }

      "init_ifRightRangeHasIncorrectLength" {
         shouldThrow<IllegalArgumentException> {
            MatchResultsOfSubLists(false, 1..2, 3..1)
         }
      }
   }
}
