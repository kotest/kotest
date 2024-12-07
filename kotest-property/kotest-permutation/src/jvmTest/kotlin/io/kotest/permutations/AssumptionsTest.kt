package io.kotest.permutations

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.exhaustive.constant
import io.kotest.property.exhaustive.ints
import io.kotest.property.exhaustive.of

class AssumptionsTest : FunSpec() {
   init {

      test("assume(boolean) should filter failing inputs") {

         permutations {

            iterations = 10

            val a by gen { Exhaustive.ints(0..10) }
            val b by gen { Exhaustive.constant(9) }

            forEach {
               assume(a != b)
               // without the assume, this would throw, because of the 9 == 9 combination would fail
               a.compareTo(b) shouldNotBe 0
            }
         }
      }

      test("assume(function) should filter failing inputs") {

         permutations {

            iterations = 10

            val a by gen { Exhaustive.ints(0..10) }
            val b by gen { Exhaustive.constant(9) }

            forEach {
               assume { a shouldNotBe b }
               // without the assume, this would throw, because of the 9 == 9 combination would fail
               a.compareTo(b) shouldNotBe 0
            }
         }
      }

      test("assume(boolean) that always filters should not deadlock due to maxDiscardPercentage") {
         shouldThrowAny {
            permutations {
               forEach {
                  assume(false)
               }
            }
         }
      }

      test("assume(function) that always filters should not deadlock due to maxDiscardPercentage") {
         shouldThrowAny {
            permutations {
               forEach {
                  assume { error("boom") }
               }
            }
         }
      }

      test("assume(fn) should support assertions") {
         permutations {

            val a by gen { Arb.string(2, Codepoint.az()) }
            val b by gen { Arb.string(2, Codepoint.az()) }

            forEach {
               assume {
                  a shouldNotBe b
                  a shouldHaveLength b.length
               }

               a.compareTo(b) shouldNotBe 0
            }
         }
      }

      test("assumptions should fail if too many are discarded") {
         // this will throw because we only have 9 combinations and 3 are disallowed
         shouldThrowAny {
            permutations {

               val a by gen { Arb.int(0..2) }
               val b by gen { Arb.int(0..2) }

               forEach {
                  assume(a != b)
               }
            }
         }
      }

      test("assumptions should honour maxDiscardPercentage config") {
         // this will pass because we upped the discard percentage
         shouldThrowAny {
            permutations {
               maxDiscardPercentage = 50
               val a by gen { Arb.int(0..2) }
               val b by gen { Arb.int(0..2) }
               forEach {
                  assume(a != b)
               }
            }
         }
      }

      test("discards counter") {

         val result1 = permutations {
            maxDiscardPercentage = 99
            val a by gen { Arb.constant("a") }
            val b by gen { Arb.constant("b") }
            forEach {
               assume(a != b)
            }
         }
         result1.discards.shouldBe(0)

         val result2 = permutations {
            maxDiscardPercentage = 99
            val a by gen { Arb.string(1, Codepoint.az()) }
            val b by gen { Arb.constant("b") }
            forEach {
               assume(a != b)
            }
         }
         result2.discards.shouldBe(3)
      }

      test("discards should not count towards iteration counts") {

         val result1 = permutations {
            maxDiscardPercentage = 99
            val a by gen { Exhaustive.of(1, 2, 3) }
            forEach {
               assume(a < 3)
            }
         }
         result1.attempts.shouldBe(1)

         val result2 = permutations {
            maxDiscardPercentage = 99
            val a by gen { Exhaustive.of(1, 2, 3, 4) }
            forEach {
               assume(a < 3)
            }
         }
         result2.attempts.shouldBe(3)
      }
   }
}
