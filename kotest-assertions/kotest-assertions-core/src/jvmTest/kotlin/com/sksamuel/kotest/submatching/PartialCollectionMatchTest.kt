package com.sksamuel.kotest.submatching

import io.kotest.assertions.submatching.MatchedCollectionElement
import io.kotest.assertions.submatching.PartialCollectionMatch
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

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

