package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.multiples
import kotlin.random.Random

class MultipleTest : FunSpec({
   test("multiples of k") {
      Arb.multiples(3, 99999).generate(Random.Default).take(100).forEach { it.value % 3 shouldBe 0 }
   }
})
