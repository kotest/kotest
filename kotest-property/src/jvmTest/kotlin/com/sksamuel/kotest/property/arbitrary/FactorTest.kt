package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.factors
import io.kotest.property.arbitrary.take

class FactorTest : FunSpec({
   test("factors of k") {
      Arb.factors(99).take(100).forEach { 99 % it shouldBe 0 }
   }
})
