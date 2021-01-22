package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bigInt
import io.kotest.property.checkAll
import java.math.BigInteger

class BigIntArbTest : ShouldSpec({
   should("Generate different big ints") {
      val generated = mutableSetOf<BigInteger>()
      Arb.bigInt(100).checkAll { generated += it }

      generated.size shouldBeGreaterThan 500
   }

   should("Generate all big ints with the same probability") {
      val generated = hashMapOf<BigInteger, Int>()
      Arb.bigInt(4, 4).checkAll(100_000) {
         generated.merge(it, 1, Int::plus)
      }
      generated.forEach {
         // Each value should be generated 100_000/2^4 times, so ~6250
         it.value.shouldBeBetween(5800, 6600)
      }
   }
})
