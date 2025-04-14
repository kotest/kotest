package io.kotest.similarity

import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder

@EnabledIf(LinuxOnlyGithubCondition::class)
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

      "different not null data classes" {
         VanillaDistanceCalculator.compare(fieldName, 41, 42) shouldBe
            AtomicMismatch(fieldName, 41, 42)
         VanillaDistanceCalculator.compare(fieldName, redCircle, otherRedCircle) shouldBe
            AtomicMismatch(fieldName, redCircle, otherRedCircle)
      }

      "different not null Strings" {
         val actual = VanillaDistanceCalculator.compare(fieldName, "Perpetuum mobile", "perpetuum mobile")
         assertSoftly {
            actual.shouldBeInstanceOf<StringMismatch>()
            actual.field shouldBe fieldName
            actual.expected shouldBe "Perpetuum mobile"
            actual.actual shouldBe "perpetuum mobile"
            actual.mismatchDescription.shouldContainInOrder(
               "Match[0]: part of slice with indexes [1..15] matched actual[1..15]",
               """Line[0] ="perpetuum mobile"""",
               """Match[0]= -+++++++++++++++"""
            )
         }
      }
   }
}
