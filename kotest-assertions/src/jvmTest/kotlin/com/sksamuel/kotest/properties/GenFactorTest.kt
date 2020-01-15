package com.sksamuel.kotest.properties

import io.kotest.core.spec.style.FunSpec
import io.kotest.properties.Gen
import io.kotest.properties.factors
import io.kotest.shouldBe

class GenFactorTest : FunSpec({
   test("factors of k") {
      Gen.factors(99).random().take(100).forEach { 99 % it shouldBe 0 }
   }
})
