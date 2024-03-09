package io.kotest.engine.js

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NestedTests : FunSpec() {
   init {
      test("a top level successful test") {
         1 + 1 shouldBe 2
      }

      test("a top level failing test") {
         1 + 1 shouldBe 3
      }

      test("a top level skipped test") {}

      context("a context") {
         test("successful test") {
            1 + 1 shouldBe 2
         }

         test("failing test") {
            1 + 1 shouldBe 3
         }

         xtest("skipped test") { }
      }

      xcontext("skipped context") {
         test("successful test") {
            1 + 1 shouldBe 2
         }

         test("failing test") {
            1 + 1 shouldBe 3
         }

         xtest("skipped test") { }
      }
   }
}
