package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class StringArbTest : FunSpec() {

   init {

      test("String arbitraries") {
         forAll(
            Arb.string(0, 10).take(10),
            Arb.string(0, 5).take(10)
         ) { a, b ->
            (a + b).length == a.length + b.length
         }
      }

      test("should honour sizes") {
         forAll(Arb.string(10..20).take(100)) {
            it.length >= 10
            it.length <= 20
         }
         forAll(Arb.string(3..8).take(100)) {
            it.length >= 3
            it.length <= 8
         }
         forAll(Arb.string(0..10).take(100)) { it.length <= 10 }
         forAll(Arb.string(0..3).take(100)) { it.length <= 3 }
         forAll(Arb.string(4..4).take(100)) { it.length == 4 }
         forAll(Arb.string(1..3).take(100)) {
            it.isNotEmpty()
            it.length <= 3
         }
      }
   }
}
