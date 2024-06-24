package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.ListMatcher
import io.kotest.matchers.collections.detailed.MatchResultsOfSubLists
import io.kotest.matchers.shouldBe

class ListsWithCustomMatchersTest: StringSpec() {
    private val sut = ListMatcher()

    init {
        "caseInsensitiveMatch" {
            val leftList = listOf("Green", "Blue", "Purple")
            val rightList = listOf("green", "BLUE", "PuRpLe")
            val actual =
                sut.match(leftList, rightList) { a: String, b: String -> a.lowercase() == b.lowercase() }
            val expected = listOf(MatchResultsOfSubLists(true, 0..2, 0..2))
            actual shouldBe expected
        }

        "caseInsensitiveMismatch" {
            val leftList = listOf("Green", "Blue", "Purple")
            val rightList = listOf("green", "BLUE", "PuRpLish")
            val actual =
                sut.match(leftList, rightList) { a: String, b: String -> a.lowercase() == b.lowercase() }
            val expected = listOf(
                MatchResultsOfSubLists(true, 0..1, 0..1),
                MatchResultsOfSubLists(false, 2..2, 2..2)
            )
            actual shouldBe expected
        }

        "matchOnOneField" {
            val leftList = listOf(RandomThing("Cucumber", 8), RandomThing("Zuccini", 10))
            val rightList = listOf(RandomThing("Pliers", 8), RandomThing("Hammer", 10))
            val actual =
                sut.match(leftList, rightList) { a: RandomThing, b: RandomThing -> a.length == b.length }
            val expected = listOf(MatchResultsOfSubLists(true, 0..1, 0..1))
            actual shouldBe expected
        }

        "mismatchOnOneField" {
            val leftList = listOf(RandomThing("Cucumber", 8), RandomThing("Zuccini", 9))
            val rightList = listOf(RandomThing("Pliers", 8), RandomThing("Hammer", 10))
            val actual =
                sut.match(leftList, rightList) { a: RandomThing, b: RandomThing -> a.length == b.length }
            val expected = listOf(
                MatchResultsOfSubLists(true, 0..0, 0..0),
                MatchResultsOfSubLists(false, 1..1, 1..1)
            )
            actual shouldBe expected
        }
    }
}

data class RandomThing(val randomName: String, val length: Int)
