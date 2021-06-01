package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

class ArrayTest : FunSpec({
   test("ByteArray should generate specified lengths") {
      Arb.byteArray(Arb.int(5, 15), Arb.byte()).take(1000).toList().forAll {
         it.size.shouldBeGreaterThanOrEqual(5)
         it.size.shouldBeLessThanOrEqual(15)
      }
   }

   test("ByteArray should populate random byte values") {
      Arb.byteArray(Arb.constant(1000000), Arb.byte()).take(10).toList().forAll {
         it.toSet().size shouldBe 256
      }
   }
})
