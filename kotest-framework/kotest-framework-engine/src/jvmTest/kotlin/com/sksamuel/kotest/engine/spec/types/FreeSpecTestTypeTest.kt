package com.sksamuel.kotest.engine.spec.types

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class FreeSpecTestTypeTest : FreeSpec() {
   init {

      "context" - {
         this.testCase.type shouldBe TestType.Container
         "context 2" - {
            this.testCase.type shouldBe TestType.Container
            "test 1" {
               this.testCase.type shouldBe TestType.Test
            }
         }
      }
      "test" {
         this.testCase.type shouldBe TestType.Test
      }
   }
}
