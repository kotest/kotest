package com.skamuel.kotest.runner.junit4.com.sksamuel.kotest.runner.junit4t

import io.kotest.core.annotation.Ignored
import io.kotest.runner.junit4.FunSpec

@Ignored
class IgnoredSpecTest : FunSpec() {
   init {
      test("foo") {
         error("splat!") // would cause a test suite failure if this class isn't successfully ignored
      }
   }
}
