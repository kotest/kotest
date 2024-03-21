package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.ItemsMatch
import io.kotest.matchers.collections.detailed.ListMatcher
import io.kotest.matchers.collections.detailed.MatchResultType
import io.kotest.matchers.collections.detailed.MatchResultsOfSubLists
import io.kotest.matchers.shouldBe

class AddItemsMatchToOneTailTest: StringSpec() {
    private val sut = ListMatcher()

    init {
        "blowsUpForEmptyTail" {
            val match = ItemsMatch(true, MatchResultType.BOTH_ELEMENTS_PRESENT)
            shouldThrow<IllegalArgumentException> {
                sut.addItemsMatchToOneTail(match, mutableListOf())
            }
        }

        "extendsMatch" {
            val match = ItemsMatch(true, MatchResultType.BOTH_ELEMENTS_PRESENT)
            val lastRangeMatch = MatchResultsOfSubLists(false, 3..4, 4..3)
            val tail = mutableListOf(MatchResultsOfSubLists(true, 1..2, 2..3), lastRangeMatch)
            val actual = sut.addItemsMatchToOneTail(match, tail)
            val expected = mutableListOf(MatchResultsOfSubLists(true, 0..2, 1..3), lastRangeMatch)
            actual shouldBe expected
        }

        "extendsMismatch" {
            val match = ItemsMatch(false, MatchResultType.BOTH_ELEMENTS_PRESENT)
            val lastRangeMatch = MatchResultsOfSubLists(true, 3..4, 4..5)
            val tail = mutableListOf(MatchResultsOfSubLists(false, 1..2, 2..3), lastRangeMatch)
            val actual = sut.addItemsMatchToOneTail(match, tail)
            val expected = mutableListOf(MatchResultsOfSubLists(false, 0..2, 1..3), lastRangeMatch)
            actual shouldBe expected
        }

        "addsMismatchBeforeMatch" {
            val match = ItemsMatch(false, MatchResultType.BOTH_ELEMENTS_PRESENT)
            val lastMatchInTail = MatchResultsOfSubLists(false, 3..4, 4..3)
            val firstMatchInTail = MatchResultsOfSubLists(true, 1..2, 2..3)
            val tail = mutableListOf(firstMatchInTail, lastMatchInTail)
            val actual = sut.addItemsMatchToOneTail(match, tail)
            val expected = mutableListOf(MatchResultsOfSubLists(false, 0..0, 1..1), firstMatchInTail, lastMatchInTail)
            actual shouldBe expected
        }

        "addsMmatchBeforeMismatch" {
            val match = ItemsMatch(true, MatchResultType.BOTH_ELEMENTS_PRESENT)
            val lastMatchInTail = MatchResultsOfSubLists(true, 3..4, 4..5)
            val firstMatchInTail = MatchResultsOfSubLists(false, 1..2, 2..3)
            val tail = mutableListOf(firstMatchInTail, lastMatchInTail)
            val actual = sut.addItemsMatchToOneTail(match, tail)
            val expected = mutableListOf(MatchResultsOfSubLists(true, 0..0, 1..1), firstMatchInTail, lastMatchInTail)
            actual shouldBe expected
        }
    }
}
