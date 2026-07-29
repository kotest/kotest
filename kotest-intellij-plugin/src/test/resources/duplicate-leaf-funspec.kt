package io.kotest.samples.gradle

import io.kotest.core.spec.style.FunSpec

class DuplicateLeafFunSpecExampleTest : FunSpec({

   context("base") {
      context("inner root") {
         test("child1") {
         }
         test("child2") {
         }
      }
   }

   context("base 2") {
      context("inner root") {
         test("child1") {
         }
         test("child2") {
         }
      }
   }
})
