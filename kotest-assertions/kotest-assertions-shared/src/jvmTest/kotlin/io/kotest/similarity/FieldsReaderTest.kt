package io.kotest.similarity

import io.kotest.core.spec.style.StringSpec
import io.kotest.similarity.FieldAndValue
import io.kotest.similarity.FieldsReader
import io.kotest.matchers.shouldBe

class FieldsReaderTest: StringSpec() {
    private val systemToTest = FieldsReader()

    init {
        "works for data class" {
            systemToTest.fieldsOf(redCircle) shouldBe listOf(
                FieldAndValue("color", "red"),
                FieldAndValue("shape", "circle"),
            )
        }

        "works for data class with private fields" {
            systemToTest.fieldsOf(redCircleWithPrivateField) shouldBe listOf(
                FieldAndValue("color", "red"),
                FieldAndValue("shape", "circle"),
            )
        }
    }
}
