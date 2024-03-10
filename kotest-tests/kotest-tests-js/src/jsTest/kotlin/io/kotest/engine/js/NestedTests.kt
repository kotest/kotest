package io.kotest.engine.js

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NestedTests : FunSpec() {
   init {
      test("a top level successful test") {
         println("running ${testCase.name.testName}")
         1 + 1 shouldBe 2
      }

      test("a top level failing test") {
         println("running ${testCase.name.testName}")
         1 + 1 shouldBe 3
      }

      xtest("a top level skipped test") {
         println("running ${testCase.name.testName}")
      }

      context("level-1-context") {
         test("successful test inside level-1-context") {
            println("running ${testCase.name.testName}")
            1 + 1 shouldBe 2
         }

         test("failing test inside level-1-context") {
            println("running ${testCase.name.testName}")
            1 + 1 shouldBe 3
         }

         xtest("skipped test inside level-1-context") {
            println("running ${testCase.name.testName}")
         }

         context("level-2-context") {
            println("running ${testCase.name.testName}")
            test("successful test inside level-2-context") {
               println("running ${testCase.name.testName}")
               1 + 1 shouldBe 2
            }

            test("failing test inside level-2-context") {
               println("running ${testCase.name.testName}")
               1 + 1 shouldBe 3
            }

            xtest("skipped test inside level-2-context") {
               println("running ${testCase.name.testName}")
            }
         }
      }

      xcontext("level-1-context-SKIPPED") {
         println("running ${testCase.name.testName}")
         test("successful test inside level-1-context-SKIPPED") {
            println("running ${testCase.name.testName}")
            1 + 1 shouldBe 2
         }

         test("failing test inside level-1-context-SKIPPED") {
            println("running ${testCase.name.testName}")
            1 + 1 shouldBe 3
         }

         xtest("skipped test inside level-1-context-SKIPPED") {
            println("running ${testCase.name.testName}")
         }
      }
   }
}
