package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.multiples

class MultipleTest : FunSpec({
   test("multiples of k") {
      Arb.multiples(3, 99999).generate(RandomSource.Default).take(100).forEach { it.value % 3 shouldBe 0 }
   }
})
