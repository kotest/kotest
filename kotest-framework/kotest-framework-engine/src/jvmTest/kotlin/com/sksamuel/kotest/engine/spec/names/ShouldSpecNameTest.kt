package com.sksamuel.kotest.engine.spec.names

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ShouldSpecNameTest : ShouldSpec() {
   init {
      context("a should spec context should have default affixes") {
         this.testCase.name.defaultAffixes shouldBe true
         should("a should spec should should have default affixes") {
            this.testCase.name.defaultAffixes shouldBe true
         }
      }
   }
}
