package io.kotest.submatching

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

//@EnabledIf(LinuxCondition::class)
class PartialCollectionMatchTest : StringSpec() {
   private val systemToTest = PartialCollectionMatch(
      MatchedCollectionElement(2, 3),
      length = 4,
   )

   init {
      "rangeOfValue" {
         systemToTest.rangeOfExpected shouldBe 2..5
      }
      "rangeOfTarget" {
         systemToTest.rangeOfValue shouldBe 3..6
      }
   }
}

