package io.kotest.property.gen

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Gen
import io.kotest.property.forAll
import io.kotest.property.take

class StringGenTest : FunSpec() {

   init {

      test("String arbitraries") {
         forAll(
            Gen.string(0..10).take(10),
            Gen.string(0..5).take(10)
         ) { a, b ->
            (a + b).length == a.length + b.length
         }
      }

      test("should honour sizes") {
         forAll(Gen.string(10..20).take(100)) {
            it.length >= 10
            it.length <= 20
         }
         forAll(Gen.string(3..8).take(100)) {
            it.length >= 3
            it.length <= 8
         }
         forAll(Gen.string(0..10).take(100)) { it.length <= 10 }
         forAll(Gen.string(0..3).take(100)) { it.length <= 3 }
         forAll(Gen.string(4..4).take(100)) { it.length == 4 }
         forAll(Gen.string(1..3).take(100)) {
            it.isNotEmpty()
            it.length <= 3
         }
      }
   }
}
