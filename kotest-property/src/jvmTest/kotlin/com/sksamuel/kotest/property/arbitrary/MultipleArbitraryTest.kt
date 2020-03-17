package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.multiples

class MultipleArbitraryTest : FunSpec() {
   init {
      test("multiple generation") {
         Arb.multiples(3, 100)
            .generate(RandomSource.Default)
            .take(100)
            .forAll { it.value % 3 shouldBe 0 }
      }
   }
}
