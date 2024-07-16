package io.kotest.submatching

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PartialCollectionMatchTest: StringSpec() {
   private val systemToTest = PartialCollectionMatch(
      MatchedCollectionElement(2, 3),
      length = 4,
      value = "buzzword".toList()
   )
   init {
      "rangeOfValue" {
         systemToTest.rangeOfValue shouldBe 2..5
      }
      "rangeOfTarget" {
         systemToTest.rangeOfTarget shouldBe 3..6
      }
      "partOfValue" {
         systemToTest.partOfValue.joinToString("") shouldBe "zzwo"
      }
   }
}

