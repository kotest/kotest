package com.sksamuel.specsa

import com.sksamuel.specsb.tests
import io.kotest.core.spec.style.FunSpec

// matches the filter 'Spec1'
class Spec1 : FunSpec() {
   init {
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
