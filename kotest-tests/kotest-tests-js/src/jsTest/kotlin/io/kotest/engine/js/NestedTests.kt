package io.kotest.engine.js

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NestedTests : FunSpec() {
   init {
      test("a top level test") {
         1 + 1 shouldBe 2
      }

      context("a context") {
         test("a test") {
            1 + 1 shouldBe 2
         }

         test("another test") {
            1 + 1 shouldBe 3
         }
      }
   }
}
