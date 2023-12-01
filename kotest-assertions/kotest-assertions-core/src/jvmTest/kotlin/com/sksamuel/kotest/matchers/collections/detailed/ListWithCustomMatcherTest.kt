package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.shouldMatchList
import io.kotest.matchers.shouldBe
import kotlin.text.lowercase

class ListWithCustomMatcherTest: StringSpec() {
    private val crew = listOf("Yoda", "Chewbacca", "Luke")
    private val sameCrewInLowerCase = crew.map { it.lowercase() }

    init {
        "matchesStringsWithCustomMatcher" {
            shouldThrow<AssertionError> {
               sameCrewInLowerCase shouldMatchList crew
            }
           sameCrewInLowerCase.shouldMatchList(crew) { a: String, b: String -> a.lowercase() == b.lowercase() }
        }

        "findsMismatchesInStringsWithCustomMatcher" {
            val anotherCrew = listOf("Yoda", "Chewbacca", "Leia")
            val thrown = shouldThrow<AssertionError> {
               anotherCrew.shouldMatchList(crew) { a: String, b: String -> a.lowercase() == b.lowercase() }
            }
            thrown.message shouldBe """
Match:
expected[0] == actual[0]: Yoda
expected[1] == actual[1]: Chewbacca

Mismatch:
expected[2] = Luke
actual[2] = Leia
"""
        }

        "matchesDoublesWithTolerance" {
            val expected = listOf(1.0, 2.0)
            val actual = listOf(0.91, 2.09)
            shouldThrow<AssertionError> {
               actual shouldMatchList expected
            }
           actual.shouldMatchList(expected) { a, b -> b in (a - 0.1)..(a + 0.1) }
        }

        "findsMismatchesInDoublesWithTolerance" {
            val expected = listOf(1.0, 2.0)
            val actual = listOf(0.949, 2.51)
            val thrown = shouldThrow<AssertionError> {
               actual.shouldMatchList(expected) { a, b -> b in (a - 0.05)..(a + 0.05) }
            }
            thrown.message shouldBe """
Mismatch:
expected[0] = 1.0
expected[1] = 2.0
actual[0] = 0.949
actual[1] = 2.51
"""
        }
    }
}
