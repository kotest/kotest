package com.sksamuel.kotest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FooTest : FunSpec() {
   init {
      test("a") {
         "qwqwee" shouldBe 1
      }
   }
}
