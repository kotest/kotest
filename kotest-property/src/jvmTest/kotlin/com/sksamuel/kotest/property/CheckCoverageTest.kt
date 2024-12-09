@file:Suppress("DEPRECATION")

package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkCoverage
import io.kotest.property.forAll

@EnabledIf(LinuxCondition::class)
class CheckCoverageTest : FunSpec({

   test("should pass if coverage met expectations") {

      checkCoverage("even", 25.0) {
         forAll(Arb.int()) { a ->
            classify(a % 2 == 0, "even", "odd")
            a + a == 2 * a
         }
      }

      checkCoverage("even" to 25.0, "odd" to 25.0) {
         forAll(Arb.int()) { a ->
            classify(a % 2 == 0, "even", "odd")
            a + a == 2 * a
         }
      }
   }

   test("should throw if coverage did not meet expectations") {

      shouldThrowAny {
         checkCoverage("zero" to 50.0) {
            forAll<Int> { a ->
               classify(a == 0, "zero", ">zero")
               a + a == 2 * a
            }
         }
      }
   }

   test("should throw if coverage is not classified at all") {

      shouldThrowAny {
         checkCoverage("not same" to 50.0) {
            forAll<Int> { a ->
               classify(a != a, "not same", "same")
               a + a == 2 * a
            }
         }
      }
   }
})
