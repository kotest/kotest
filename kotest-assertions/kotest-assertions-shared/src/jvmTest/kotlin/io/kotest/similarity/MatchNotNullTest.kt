package io.kotest.similarity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MatchNotNullTest: StringSpec() {
    private val fieldName = "field"
    init {
        "match for primitives" {
            matchNotNull(fieldName, "this", "this") shouldBe Match(fieldName, "this")
        }

        "mismatch for primitives" {
            matchNotNull(fieldName, "this", "another") shouldBe AtomicMismatch(fieldName, "this", "another")
        }

        "match for identical data class instances" {
            val anotherRedCircle = redTriangle.copy(shape = "circle")
            matchNotNull(fieldName, redCircle, anotherRedCircle) shouldBe Match(fieldName, redCircle)
        }

        "mismatch for data class instances of different types" {
            matchNotNull(fieldName, redCircle, otherRedCircle) shouldBe AtomicMismatch(fieldName, redCircle, otherRedCircle)
        }
    }
}
