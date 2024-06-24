package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.MatchResultsOfSubLists
import io.kotest.matchers.collections.detailed.bestTwoMatches
import io.kotest.matchers.shouldBe

class BestTwoMatchesTest: StringSpec() {

    private val matches1 = listOf(
            MatchResultsOfSubLists(true, 1..3, 3..5),
            MatchResultsOfSubLists(false, 4..5, 6..5),
            MatchResultsOfSubLists(true, 6..9, 6..9)
    )

    private val matches2 = listOf(
            MatchResultsOfSubLists(true, 1..3, 3..5),
            MatchResultsOfSubLists(false, 4..4, 6..5),
            MatchResultsOfSubLists(true, 5..5, 6..6),
            MatchResultsOfSubLists(false, 6..5, 7..8),
            MatchResultsOfSubLists(true, 6..7, 9..10)
    )

    private val matches3 = listOf(
            MatchResultsOfSubLists(false, 0..0, 0..2),
            MatchResultsOfSubLists(true, 1..2, 3..4),
            MatchResultsOfSubLists(false, 3..4, 5..5),
            MatchResultsOfSubLists(true, 5..9, 6..10)
    )

    private val matches4 = listOf(
            MatchResultsOfSubLists(false, 0..0, 0..2),
            MatchResultsOfSubLists(true, 1..2, 3..4),
            MatchResultsOfSubLists(false, 3..4, 5..5),
            MatchResultsOfSubLists(true, 5..5, 6..6),
            MatchResultsOfSubLists(false, 6..5, 7..8),
            MatchResultsOfSubLists(true, 6..9, 9..12)
    )

    init {
        "bestTwoMatches_handlesEmptyList" {
            val actual = bestTwoMatches(emptyList())
            actual.isEmpty() shouldBe true
        }

        "bestTwoMatches_onlyTailStartingWithMatch" {
            val actual = bestTwoMatches(listOf(matches1, matches2))
            actual shouldBe listOf(matches1)
        }

        "bestTwoMatches_onlyTailStartingWithMismatch" {
            val actual = bestTwoMatches(listOf(matches3, matches4))
            actual shouldBe listOf(matches3)
        }

        "bestTwoMatches_twoTails" {
            val actual = bestTwoMatches(listOf(matches1, matches2, matches3, matches4))
            actual shouldBe listOf(matches1, matches3)
        }
    }
}
