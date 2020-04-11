package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec

class FunSpecContextFocusTest : FunSpec() {
   init {
      context("f:my test 1") {
         test("something 1") { }
      }
      context("my test 2") {
         test("blabla") { error("boom") }
      }
   }
}

class FunSpecTestFocusTest : FunSpec() {
   init {
      test("f:my test 1") { }
      test("my test 2") {
         error("boom")
      }
   }
}
