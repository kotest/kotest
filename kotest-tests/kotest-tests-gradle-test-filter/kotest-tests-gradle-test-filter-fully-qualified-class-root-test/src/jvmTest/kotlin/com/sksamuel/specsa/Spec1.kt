package com.sksamuel.specsa

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

var tests = 0

// only root should run here as the filter specifies a fully qualified class with a test name
class Spec1 : FunSpec() {
   init {
      afterProject {
         tests shouldBe 1
      }
      test("root") { tests++ }
      context("context") {
         test("whack!") {
            error("whack!")
         }
         test("nested") { tests++ }
      }
      test("splat!") {
         error("splat1")
      }
   }
}
