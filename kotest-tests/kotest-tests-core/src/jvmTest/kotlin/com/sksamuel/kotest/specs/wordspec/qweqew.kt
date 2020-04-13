package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

internal class BlaBlaTest : FunSpec() {

   init {
      context("f:some test") {
         test("should run") {
            1 shouldBe 1
         }
         test("should run 2") {
            2 shouldBe 1
         }
      }
      context("other test") {
         test("should never run") {
            3 shouldBe 4
         }
      }
   }
}
