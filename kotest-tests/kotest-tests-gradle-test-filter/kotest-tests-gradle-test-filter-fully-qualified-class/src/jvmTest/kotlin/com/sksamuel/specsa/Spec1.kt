package com.sksamuel.specsa

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

var tests = 0

class Spec1 : FunSpec() {
   init {
      afterProject {
         tests shouldBe 2
      }
      test("root") { tests++ }
      context("context") {
         test("nested") { tests++ }
      }
   }
}
