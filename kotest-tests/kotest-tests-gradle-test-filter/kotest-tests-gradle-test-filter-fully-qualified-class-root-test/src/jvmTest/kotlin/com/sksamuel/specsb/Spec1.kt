package com.sksamuel.specsb

import io.kotest.core.spec.style.FunSpec

// should be ignored as the package does not match the filter even tho the classname and test do
class Spec1 : FunSpec() {
   init {
      test("root") { error("boom") }
   }
}
