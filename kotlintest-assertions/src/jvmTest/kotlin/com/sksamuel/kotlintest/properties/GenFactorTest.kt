package com.sksamuel.kotlintest.properties

import io.kotlintest.properties.Gen
import io.kotlintest.properties.factors
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class GenFactorTest : FunSpec({
   test("factors of k") {
      Gen.factors(99).random().take(100).forEach { 99 % it shouldBe 0 }
   }
})
