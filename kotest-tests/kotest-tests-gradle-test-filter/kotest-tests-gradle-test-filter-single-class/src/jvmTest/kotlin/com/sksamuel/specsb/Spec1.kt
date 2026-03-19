package com.sksamuel.specsb

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

var tests = 0

// matches the filter 'Spec1'
class Spec1 : FunSpec() {
   init {
      afterProject {
         tests shouldBe 4
      }
      test("root") { tests++ }
      context("context") {
         test("nested") { tests++ }
      }
   }
}

// should be ignored completely as the filter is 'Spec1'
class Spec2 : FunSpec() {
   init {
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
