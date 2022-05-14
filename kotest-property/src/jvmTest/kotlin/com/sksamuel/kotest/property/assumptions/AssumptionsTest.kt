package com.sksamuel.kotest.property.assumptions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.MaxDiscardPercentageException
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.withAssumption

class AssumptionsTest : FunSpec() {
   init {
      test("assumptions should filter failing inputs") {

         // this will throw because for 10000 combinations some 2 letter string should match
         shouldThrowAny {
            checkAll(10000, Arb.string(2, Codepoint.az()), Arb.string(2, Codepoint.az())) { a, b ->
               a.compareTo(b) shouldNotBe 0
            }
         }

         // this will now pass because the assumption will filter out equal strings
         checkAll(10000, Arb.string(2, Codepoint.az()), Arb.string(2, Codepoint.az())) { a, b ->
            withAssumption(a != b) {
               a.compareTo(b) shouldNotBe 0
            }
         }
      }

      test("assumptions should fail if too many are discarded") {
         // this will throw because we only have 9 combinations and 3 are disallowed
         shouldThrow<MaxDiscardPercentageException> {
            checkAll(PropTestConfig(), Arb.int(0..2), Arb.int(0..2)) { a, b ->
               withAssumption(a != b) {
               }
            }
         }
      }

      test("assumptions should honour maxDiscardPercentage config") {
         // this will pass because we upped the discard percentage
         checkAll(PropTestConfig(maxDiscardPercentage = 50), Arb.int(0..3), Arb.int(0..3)) { a, b ->
            withAssumption(a != b) {
            }
         }
      }

      test("discard percentage calcuation") {

         val c1 = checkAll(PropTestConfig(maxDiscardPercentage = 100), Arb.constant("a"), Arb.constant("a")) { a, b ->
            withAssumption(a != b) {
            }
         }
         c1.discardPercentage().shouldBe(100)

         val c2 = checkAll(PropTestConfig(maxDiscardPercentage = 1), Arb.constant("a"), Arb.constant("b")) { a, b ->
            withAssumption(a != b) {
            }
         }
         c2.discardPercentage().shouldBe(0)

         val c3 = checkAll(
            PropTestConfig(seed = 234),
            Arb.string(1, Codepoint.az()),
            Arb.constant("a")
         ) { a, b ->
            withAssumption(a != b) {
            }
         }
         c3.discardPercentage().shouldBe(3)
      }

      test("nested assumptions") {
         checkAll(PropTestConfig(maxDiscardPercentage = 75), Arb.int(0..1), Arb.int(0..3)) { a, b ->
            withAssumption(a != b) {
               withAssumption(a == 1) {
                  a shouldBe 1
                  b shouldNotBe 1
               }
            }
         }
      }
   }
}
