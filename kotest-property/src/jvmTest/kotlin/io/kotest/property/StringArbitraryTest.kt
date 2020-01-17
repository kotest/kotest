package io.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.arbitrary.string

class StringArbitraryTest : FunSpec() {

   init {

      test("String arbitraries") {
         forAll(
            Arbitrary.string(0, 10),
            Arbitrary.string(0, 5)
         ) { a, b ->
            (a + b).length == a.length + b.length
         }
      }

      test("should honour min size") {
         forAll(Arbitrary.string(minSize = 10)) { it.length >= 10 }
         forAll(Arbitrary.string(minSize = 3)) { it.length >= 3 }
         forAll(Arbitrary.string(minSize = 1)) { it.isNotEmpty() }
      }

      test("should honour max size") {
         forAll(Arbitrary.string(maxSize = 10)) { it.length <= 10 }
         forAll(Arbitrary.string(maxSize = 3)) { it.length <= 3 }
         forAll(Arbitrary.string(maxSize = 1)) { it.length <= 1 }
      }

      test("should honour min and max size") {
         forAll(Arbitrary.string(minSize = 10, maxSize = 10)) { it.length == 10 }
         forAll(Arbitrary.string(minSize = 1, maxSize = 3)) {
            it.isNotEmpty()
            it.length <= 3
         }
         forAll(Arbitrary.string(minSize = 1, maxSize = 1)) { it.length == 1 }
         forAll(Arbitrary.string(minSize = 4, maxSize = 4)) { it.length == 4 }
      }
   }
}
