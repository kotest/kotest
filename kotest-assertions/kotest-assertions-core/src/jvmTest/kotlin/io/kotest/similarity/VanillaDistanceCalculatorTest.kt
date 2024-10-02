package io.kotest.similarity

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class VanillaDistanceCalculatorTest : StringSpec() {
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
