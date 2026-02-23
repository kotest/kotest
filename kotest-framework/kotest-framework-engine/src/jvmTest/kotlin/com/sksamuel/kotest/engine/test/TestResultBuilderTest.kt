package com.sksamuel.kotest.engine.test

import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import io.kotest.matchers.shouldBe

class TestResultBuilderTest : FreeSpec() {
   init {
      "ignored without reason" {
         TestResultBuilder.builder().withIgnore().build() shouldBe TestResult.Ignored
      }
      "ignored with reason" {
         TestResultBuilder.builder().withIgnoreReason("wobble").build() shouldBe TestResult.Ignored("wobble")
      }
   }
}
