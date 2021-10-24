package com.sksamuel.kotest.engine.spec.types

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class ExpectSpecTestTypeTest : ExpectSpec() {
   init {
      context("context") {
         this.testCase.type shouldBe TestType.Container
         context("context 2") {
            this.testCase.type shouldBe TestType.Container
            expect("test") {
               this.testCase.type shouldBe TestType.Test
            }
         }
         expect("test") {
            this.testCase.type shouldBe TestType.Test
         }
      }
   }
}
