package com.sksamuel.kotest.engine.spec.types

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class BehaviorSpecTestTypeTest : BehaviorSpec() {
   init {
      given("given") {
         this.testCase.type shouldBe TestType.Container
         `when`("when") {}
         When("when") {
            this.testCase.type shouldBe TestType.Container
            then("test") {
               this.testCase.type shouldBe TestType.Test
            }
         }
         then("test 2") {
            this.testCase.type shouldBe TestType.Test
         }
         and("and") {
            this.testCase.type shouldBe TestType.Container
            then("then 3") {
               this.testCase.type shouldBe TestType.Test
            }
         }
      }
   }
}
