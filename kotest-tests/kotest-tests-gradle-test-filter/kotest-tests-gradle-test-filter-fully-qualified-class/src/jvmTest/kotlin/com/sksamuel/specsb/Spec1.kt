package com.sksamuel.specsb

import com.sksamuel.specsa.tests
import io.kotest.core.spec.style.FunSpec

// should be ignored completely as the filter specifies a fully qualified class name
class Spec1 : FunSpec() {
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
