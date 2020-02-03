package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.multiples
import kotlin.random.Random

class MultipleArbitraryTest : FunSpec() {
   init {
      test("multiple generation") {
         Arb.multiples(3, 100)
            .generate(Random.Default)
            .take(100)
            .forAll { it.value % 3 shouldBe 0 }
      }
   }
}
