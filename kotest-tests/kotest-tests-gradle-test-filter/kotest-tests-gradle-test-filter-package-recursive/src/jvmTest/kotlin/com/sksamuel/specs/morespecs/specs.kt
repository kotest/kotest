package com.sksamuel.specs.morespecs

import com.sksamuel.specs.tests
import io.kotest.core.spec.style.FunSpec

class Spec1 : FunSpec() {
   init {
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
