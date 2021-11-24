package com.sksamuel.kotest.engine.spec.types

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class StringSpecTestTypeTest : StringSpec() {
   init {
      "context" {
         this.testCase.type shouldBe TestType.Test
      }
   }
}
