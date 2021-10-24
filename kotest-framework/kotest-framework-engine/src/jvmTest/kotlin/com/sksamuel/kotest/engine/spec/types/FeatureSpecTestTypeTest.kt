package com.sksamuel.kotest.engine.spec.types

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class FeatureSpecTestTypeTest : FeatureSpec() {
   init {
      feature("context") {
         this.testCase.type shouldBe TestType.Container
         feature("context 2") {
            this.testCase.type shouldBe TestType.Container
            scenario("test") {
               this.testCase.type shouldBe TestType.Test
            }
         }
         scenario("test") {
            this.testCase.type shouldBe TestType.Test
         }
      }
   }
}
