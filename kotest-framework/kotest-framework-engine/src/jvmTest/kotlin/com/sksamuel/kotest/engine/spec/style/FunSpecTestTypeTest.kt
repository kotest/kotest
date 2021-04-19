package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class FunSpecTestTypeTest : FunSpec() {
   init {

      finalizeSpec { it.b.size shouldBe 4 }

      context("context") {
         this.testCase.type shouldBe TestType.Container
         context("context 2") {
            this.testCase.type shouldBe TestType.Container
            test("test") {
               this.testCase.type shouldBe TestType.Test
            }
         }
         test("test") {
            this.testCase.type shouldBe TestType.Test
         }
      }
   }
}
