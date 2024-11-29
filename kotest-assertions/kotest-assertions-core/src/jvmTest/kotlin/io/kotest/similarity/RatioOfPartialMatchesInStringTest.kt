package io.kotest.similarity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.submatching.MatchedCollectionElement
import io.kotest.submatching.PartialCollectionMatch
import java.math.BigDecimal

class RatioOfPartialMatchesInStringTest: StringSpec() {
   init {
      "almost complete match" {
         val expected = "0123456789abcdefghijklmnopqrstuvwxyz"
         val actual = "$expected?"
         ratioOfPartialMatchesInString(
            listOf( PartialCollectionMatch(
               MatchedCollectionElement(0, 0),
               length = expected.length
            ),
            ),
            expected,
            actual
         ) shouldBe BigDecimal("0.97")
      }
   }
}

