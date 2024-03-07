package io.kotest.similarity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TopWithTiesByTest: StringSpec() {
    init {
        "handle empty sequence" {
            sequenceOf<Int>().topWithTiesBy { it } shouldBe listOf<Int>()
        }

        "handle one element" {
            sequenceOf(1).topWithTiesBy { it } shouldBe listOf<Int>(1)
        }

        "handle two elements" {
            sequenceOf(1, 1).topWithTiesBy { it } shouldBe listOf<Int>(1, 1)
        }

        "find one top match" {
            sequenceOf(oneApple, twoApples, oneOrange).topWithTiesBy { it.count } shouldBe listOf(twoApples)
        }

        "find two top matches" {
            sequenceOf(oneApple, twoApples, oneOrange, twoOranges).topWithTiesBy { it.count } shouldBe
                    listOf(twoApples, twoOranges)
        }
    }
}
