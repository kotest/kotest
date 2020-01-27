package com.sksamuel.kotest.properties

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.properties.Gen
import io.kotest.properties.factors

class GenFactorTest : FunSpec({
   test("factors of k") {
      Gen.factors(99).random().take(100).forEach { 99 % it shouldBe 0 }
   }
})
