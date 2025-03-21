package io.kotest.similarity

import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal

@EnabledIf(NotMacOnGithubCondition::class)
class MatchNotNullTest : StringSpec() {
   private val fieldName = "field"

   init {
      "match for primitives" {
         matchNotNull(fieldName, "this", "this") shouldBe Match(fieldName, "this")
      }

      "mismatch for primitives" {
         matchNotNull(fieldName, BigDecimal.ONE, BigDecimal.TEN) shouldBe
            AtomicMismatch(fieldName, BigDecimal.ONE, BigDecimal.TEN)
      }

      "match for identical data class instances" {
         val anotherRedCircle = redTriangle.copy(shape = "circle")
         matchNotNull(fieldName, redCircle, anotherRedCircle) shouldBe Match(fieldName, redCircle)
      }

      "mismatch for data class instances of different types" {
         matchNotNull(fieldName, redCircle, otherRedCircle) shouldBe AtomicMismatch(
            fieldName,
            redCircle,
            otherRedCircle
         )
      }


      "mismatch for two different strings" {
         val expectedStr = "0123456789abcdefghijklmnopqrstuvwxyz"
         val actualStr = "$expectedStr?"
         val actual = matchNotNull(fieldName, expectedStr, actualStr)
         assertSoftly {
            actual.match shouldBe false
            actual.shouldBeInstanceOf<StringMismatch>()
            actual.description().shouldContainInOrder(
               "Match[0]: whole slice matched actual[0..35]",
               """Line[0] ="0123456789abcdefghijklmnopqrstuvwxyz?"""",
               """Match[0]= ++++++++++++++++++++++++++++++++++++-"""
            )
            (actual as StringMismatch).distance shouldBe Distance(BigDecimal("0.97"))
         }
      }

   }
}
