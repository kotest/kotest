package com.sksamuel.kotest.js

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class ValidateEmailTest : FunSpec() {
   init {
      test("valid emails") {
         listOf(
            "sam@sam.com",
            "my.name@some.domain.com"
         ).forAll {
            validateEmail(it) shouldBe true
         }
      }

      test("invalid emails") {
         listOf(
            "sam@sam",
            "my.name@"
         ).forAll {
            validateEmail(it) shouldBe false
         }
      }
   }
}
