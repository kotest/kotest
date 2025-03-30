package com.sksamuel.kotest.submatching

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.submatching.MatchedCollectionElement
import io.kotest.submatching.PartialCollectionMatch

@EnabledIf(LinuxOnlyGithubCondition::class)
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

