package com.sksamuel.kotest.matchers.collections.detailed.distance

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.distance.AtomicMismatch
import io.kotest.matchers.collections.detailed.distance.Match
import io.kotest.matchers.collections.detailed.distance.VanillaDistanceCalculator
import io.kotest.matchers.shouldBe

class VanillaDistanceCalculatorTest: StringSpec() {
    private val fieldName = "field"
    init {
        "null to null" {
            VanillaDistanceCalculator.compare(fieldName, null, null) shouldBe
                    Match(fieldName, null)
        }

        "null to not null" {
            VanillaDistanceCalculator.compare(fieldName, null, 42) shouldBe
                    AtomicMismatch(fieldName, null, 42)
            VanillaDistanceCalculator.compare(fieldName, 42, null) shouldBe
                    AtomicMismatch(fieldName, 42, null)
        }

        "different not nulls" {
            VanillaDistanceCalculator.compare(fieldName, 41, 42) shouldBe
                    AtomicMismatch(fieldName, 41, 42)
            VanillaDistanceCalculator.compare(fieldName, redCircle, otherRedCircle) shouldBe
                    AtomicMismatch(fieldName, redCircle, otherRedCircle)
        }
    }
}
