package io.kotest.similarity

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal

class MatchNotNullStringsTest: StringSpec() {
   init {
      "work for same string" {
         matchNotNullStrings("field", "hello", "hello") shouldBe Match("field", "hello")
      }
      "work for two different strings with similarity above threshold" {
         val expectedStr = "0123456789abcdefghijklmnopqrstuvwxyz"
         val actualStr = "$expectedStr?"
         val actual = matchNotNullStrings("field", expectedStr, actualStr)
         assertSoftly {
            actual.match shouldBe false
            actual.description().shouldContainInOrder(
               "Match[0]: whole slice matched actual[0..35]",
               """Line[0] ="0123456789abcdefghijklmnopqrstuvwxyz?"""",
               """Match[0]= ++++++++++++++++++++++++++++++++++++-"""
            )
            actual.shouldBeInstanceOf<StringMismatch>()
            (actual as StringMismatch).distance shouldBe Distance(BigDecimal("0.97"))
         }
      }
      "work for two different strings with similarity below threshold" {
         val actual = matchNotNullStrings("field", "0123456789abcdefghijklmnopqrstuvwxyz", "SFGHSRGHSFDGHSFGHSDFGHSDFGHDF")
         assertSoftly {
            actual.match shouldBe false
            actual.description() shouldBe """    "field" expected: <"0123456789abcdefghijklmnopqrstuvwxyz">, but was: <"SFGHSRGHSFDGHSFGHSDFGHSDFGHDF">"""
            actual.shouldBeInstanceOf<AtomicMismatch>()
            (actual as AtomicMismatch).distance shouldBe Distance(BigDecimal("0.00"))
         }
      }
   }
}
