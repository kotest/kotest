package com.sksamuel.kotest.example.javascript

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.shouldBe

class SsnTest : FunSpec({

   test("valid ssns") {
      validateSocial("123-456-1111") shouldBe true
      validateSocial("444-235-6453") shouldBe true
   }

   test("invalid ssn") {
      listOf("a12-456-cccc", "", "123-4561117", "122", "1234567899").forAll {
         validateSocial(it) shouldBe false
      }
   }
})
