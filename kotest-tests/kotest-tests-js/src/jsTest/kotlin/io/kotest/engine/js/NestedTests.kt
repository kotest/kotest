package io.kotest.engine.js

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NestedTests : FunSpec() {
   init {
      test("a top level successful test") {
         1 + 1 shouldBe 2
      }

      xtest("a top level failing test") {
         1 + 1 shouldBe 3
      }

      xtest("a top level skipped test") {}

      context("a context") {
         test("successful test inside a context") {
            1 + 1 shouldBe 2
         }

         test("failing test inside a context") {
            1 + 1 shouldBe 3
         }

         xtest("skipped test inside a context") { }
      }

      xcontext("skipped context") {
         test("successful test inside a context") {
            1 + 1 shouldBe 2
         }

         test("failing test inside a context") {
            1 + 1 shouldBe 3
         }

         xtest("skipped test inside a context") { }
      }
   }
}
