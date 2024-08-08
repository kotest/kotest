package com.sksamuel.kotest.engine.spec.types

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class ShouldSpecTestTypeTest : ShouldSpec() {
   init {

      context("context") {
         this.testCase.type shouldBe TestType.Container
         context("context 2") {
            this.testCase.type shouldBe TestType.Container
            should("should") {
               this.testCase.type shouldBe TestType.Test
            }
         }
         should("should") {
            this.testCase.type shouldBe TestType.Test
         }
      }
   }
}
