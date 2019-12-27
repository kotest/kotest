package io.kotest.property.arbitrary

import io.kotest.property.Arbitrary
import io.kotest.specs.FunSpec
import kotlin.random.Random

class MultipleArbitraryTest : FunSpec() {
   init {
      test("multiple generation") {
         Arbitrary.multiples(1000, 3, 100)
            .samples(Random.Default).toList()
            .forAll { it.value % 3 == 0 }
      }
   }
}
