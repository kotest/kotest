package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkCoverage
import io.kotest.property.forAll

class CheckCoverageTest : FunSpec({
   test("should throw if coverage did not meet expectations") {

      checkCoverage(25.0, "even") {
         checkCoverage(25.0, "odd") {
            forAll(Arb.int()) { a ->
               classify(a % 2 == 0, "even", "odd")
               a + a == 2 * a
            }
         }
      }

      shouldThrowAny {
         checkCoverage(50.0, "zero") {
            forAll<Int> { a ->
               classify(a == 0, "zero", ">zero")
               a + a == 2 * a
            }
         }
      }
   }
})
