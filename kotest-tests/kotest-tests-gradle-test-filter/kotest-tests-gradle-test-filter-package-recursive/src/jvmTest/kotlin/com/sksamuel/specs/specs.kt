package com.sksamuel.specs

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

var tests = 0

class Spec1 : FunSpec() {
   init {
      afterProject {
         tests shouldBe 8
      }
      test("root") { tests++ }
      context("context") {
         test("nested") { tests++ }
      }
   }
}

class Spec2 : FunSpec() {
   init {
      test("root") { tests++ }
      context("context") {
         test("nested") { tests++ }
      }
   }
}
