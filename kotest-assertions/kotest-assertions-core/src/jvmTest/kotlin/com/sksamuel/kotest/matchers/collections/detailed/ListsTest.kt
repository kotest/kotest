package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.ListMatcher
import io.kotest.matchers.collections.detailed.MatchResultsOfSubLists
import io.kotest.matchers.collections.detailed.TailOfList
import io.kotest.matchers.shouldBe

class ListsTest: StringSpec() {
    private val sut = ListMatcher()

    val leftList = listOf("Cover Letter for TPS Report",
            "TPS Report, Page 1",
            "TPS Report, Page 2")
    val rightList = listOf("TPS Report, Page 1",
            "TPS Report, Page 2",
            "Cover Letter for TPS Report")
    val shortLeftList = listOf(
            "TPS Report, Page 1",
            "TPS Report, Page 2")

    init {
        "whenBothSublistsOver" {
            val endedLeftSubList = TailOfList(leftList, leftList.size)
            val endedRightSubList = TailOfList(rightList, rightList.size)
            val actual = sut.matches(endedLeftSubList, endedRightSubList, matcher = Any::equals)
            val expected = listOf(mutableListOf(MatchResultsOfSubLists(false, 3..2, 3..2)))
            actual shouldBe expected
        }

        "whenBothSublistsOnLastItemAndMatch" {
            val lastItemOfLeftList = TailOfList(leftList, leftList.size - 1)
            val lastItemOfShortLeftList = TailOfList(shortLeftList, shortLeftList.size - 1)
            val actual = sut.matches(lastItemOfLeftList, lastItemOfShortLeftList, matcher = Any::equals)
            val expected = listOf(mutableListOf(MatchResultsOfSubLists(true, 2..2, 1..1)))
            actual shouldBe expected
        }

        "whenBothSublistsOnLastItemAndMismatch" {
            val lastItemOfLeftList = TailOfList(leftList, leftList.size - 1)
            val lastItemOfRightList = TailOfList(rightList, rightList.size - 1)
            val actual = sut.matches(lastItemOfLeftList, lastItemOfRightList, matcher = Any::equals)
            val expected = listOf(mutableListOf(MatchResultsOfSubLists(false, 2..2, 2..2)))
            actual shouldBe expected
        }

        "whenLeftSublistOver" {
            val endedLeftSubList = TailOfList(leftList, leftList.size)
            val rightSubList = TailOfList(rightList, rightList.size - 1)
            val actual = sut.matches(endedLeftSubList, rightSubList, matcher = Any::equals)
            val expected = listOf(mutableListOf(MatchResultsOfSubLists(false, 3..2, 2..2)))
            actual shouldBe expected
        }

        "whenRightSublistOver" {
            val endedLeftTailOfList = TailOfList(leftList, leftList.size - 1)
            val rightSubList = TailOfList(rightList, rightList.size)
            val actual = sut.matches(endedLeftTailOfList, rightSubList, matcher = Any::equals)
            val expected = listOf(mutableListOf(MatchResultsOfSubLists(false, 2..2, 3..2)))
            actual shouldBe expected
        }

        "completeMatch" {
            val left = TailOfList(leftList)
            val actual = sut.matches(left, left, matcher = Any::equals)
            val expected = listOf(mutableListOf(MatchResultsOfSubLists(true, 0..2, 0..2)))
            actual shouldBe expected
        }

        "mismatch" {
            val left = TailOfList(leftList)
            val right = TailOfList(rightList)
            val actual = sut.matches(left, right, matcher = Any::equals)
            val expected = listOf(
                listOf(
                    MatchResultsOfSubLists(false, 0..0, -1..-2),
                    MatchResultsOfSubLists(true, 1..2, 0..1),
                    MatchResultsOfSubLists(false, 3..2, 2..2)
                )
            )
            actual shouldBe expected
        }

        "twoShortLists" {
            val left = TailOfList(listOf("A", "C"))
            val right = TailOfList(listOf("B", "C"))
            val actual = sut.matches(left, right, matcher = Any::equals)
            val expected = listOf(
                listOf(
                    MatchResultsOfSubLists(false, 0..0, 0..0),
                    MatchResultsOfSubLists(true, 1..1, 1..1)
                )
            )
            actual shouldBe expected
        }

        "twoLongerLists" {
            val left = TailOfList(listOf("A", "B", "E"))
            val right = TailOfList(listOf("C", "D", "E"))
            val actual = sut.matches(left, right, matcher = Any::equals)
            val expected = listOf(
                listOf(
                    MatchResultsOfSubLists(false, 0..1, 0..1),
                    MatchResultsOfSubLists(true, 2..2, 2..2)
                )
            )
            actual shouldBe expected
        }

        "mismatchInTheMiddle" {
            val left = TailOfList(listOf("A", "B", "E"))
            val right = TailOfList(listOf("A", "D", "E"))
            val actual = sut.matches(left, right, matcher = Any::equals)
            val expected = listOf(
                listOf(
                    MatchResultsOfSubLists(true, 0..0, 0..0),
                    MatchResultsOfSubLists(false, 1..1, 1..1),
                    MatchResultsOfSubLists(true, 2..2, 2..2)
                )
            )
            actual shouldBe expected
        }

        "twoEvenLongerLists" {
            val left = TailOfList(listOf("A", "B", "E", "E"))
            val right = TailOfList(listOf("A", "C", "D", "E", "E"))
            val actual = sut.matches(left, right, matcher = Any::equals)
            val expected = listOf(
                listOf(
                    MatchResultsOfSubLists(true, 0..0, 0..0),
                    MatchResultsOfSubLists(false, 1..1, 1..2),
                    MatchResultsOfSubLists(true, 2..3, 3..4)
                )
            )
            actual shouldBe expected
        }

        "twoEvenLongerListsReversed" {
            val left = TailOfList(listOf("A", "B", "E", "E"))
            val right = TailOfList(listOf("A", "C", "D", "E", "E"))
            val actual = sut.matches(right, left, matcher = Any::equals)
            val expected = listOf(
                listOf(
                    MatchResultsOfSubLists(true, 0..0, 0..0),
                    MatchResultsOfSubLists(false, 1..2, 1..1),
                    MatchResultsOfSubLists(true, 3..4, 2..3)
                )
            )
            actual shouldBe expected
        }
    }
}
