package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.extendLeft
import io.kotest.matchers.collections.detailed.isNotEmpty
import io.kotest.matchers.collections.detailed.spawnRangeOnLeft
import io.kotest.matchers.shouldBe

class ClosedRangeUtilsTest: StringSpec() {
    private val emptyRange = 2..1
    private val notEmptyRange = 2..2

    init {
        "Guardian assumptions" {
            assertSoftly {
                emptyRange.isEmpty() shouldBe true
                notEmptyRange.isNotEmpty() shouldBe true
                emptyRange.isNotEmpty() shouldBe false
                notEmptyRange.isEmpty() shouldBe false
            }
        }

        "extendLeft_empty" {
            val actual = emptyRange.extendLeft()
            val expected = 1..1
            actual shouldBe expected
        }

        "extendLeft_notEmpty" {
            val actual = notEmptyRange.extendLeft()
            val expected = 1..2
            actual shouldBe expected
        }

        "spawnRangeOnLeft_notEmptyOffEmpty" {
            val actual = emptyRange.spawnRangeOnLeft(false)
            val expected = 1..1
            actual shouldBe expected
        }

        "spawnRangeOnLeft_notEmptyOffNotEmpty" {
            val actual = notEmptyRange.spawnRangeOnLeft(false)
            val expected = 1..1
            actual shouldBe expected
        }

        "spawnRangeOnLeft_emptyOffEmpty" {
            val actual = emptyRange.spawnRangeOnLeft(true)
            val expected = 1..0
            actual shouldBe expected
        }

        "spawnRangeOnLeft_emptyOffNotEmpty" {
            val actual = notEmptyRange.spawnRangeOnLeft(true)
            val expected = 1..0
            actual shouldBe expected
        }
    }
}
