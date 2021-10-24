package com.sksamuel.kotest.engine.spec.types

import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class WordSpecTestTypeTest : WordSpec() {
   init {
      "should" should {
         this.testCase.type shouldBe TestType.Container
         "test 1" {
            this.testCase.type shouldBe TestType.Test
         }
      }
      "when" `when` {
         this.testCase.type shouldBe TestType.Container
         "nested should" should {
            this.testCase.type shouldBe TestType.Container
            "test 2" {
               this.testCase.type shouldBe TestType.Test
            }
         }
      }
   }
}
