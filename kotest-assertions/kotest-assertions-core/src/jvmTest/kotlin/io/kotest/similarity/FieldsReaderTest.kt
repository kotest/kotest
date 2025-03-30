package io.kotest.similarity

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class FieldsReaderTest : StringSpec() {
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
