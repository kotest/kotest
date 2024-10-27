@file:Suppress("KotlinConstantConditions")

package io.kotest.property.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.property.Arb
import io.kotest.property.MaxDiscardPercentageException
import io.kotest.property.arbitrary.*
import io.kotest.property.exhaustive.of

class AssumptionsTest : FunSpec() {
   init {

      test("assume(boolean) should filter failing inputs") {

         shouldThrowAny {
            permutations {

               val a by gen { Arb.string(2, Codepoint.az()) }
               val b by gen { Arb.string(2, Codepoint.az()) }

               assume(a != b)

               // without the assume, this would throw, because at least one 2 letter string will match
               a.compareTo(b) shouldNotBe 0
            }
         }

         permutations {

            val a by gen { Arb.string(2, Codepoint.az()) }
            val b by gen { Arb.string(2, Codepoint.az()) }

            assume(a != b)

            // with the assume, this will pass, because the assumption will filter out equal strings
            a.compareTo(b) shouldNotBe 0
         }
      }

      test("assume(function) should filter failing inputs") {

         shouldThrowAny {
            permutations {

               val a by gen { Arb.string(2, Codepoint.az()) }
               val b by gen { Arb.string(2, Codepoint.az()) }

               assume { a != b }

               // without the assume, this would throw, because at least one 2 letter string will match
               a.compareTo(b) shouldNotBe 0
            }
         }

         permutations {

            val a by gen { Arb.string(2, Codepoint.az()) }
            val b by gen { Arb.string(2, Codepoint.az()) }

            assume { a != b }

            // with the assume, this will pass, because the assumption will filter out equal strings
            a.compareTo(b) shouldNotBe 0
         }
      }

      test("assume(boolean) that always filters should not deadlock due to maxDiscardPercentage") {
         shouldThrowAny {
            permutations {
               assume(false)
            }
         }
      }

      test("assume(function) that always filters should not deadlock due to maxDiscardPercentage") {
         shouldThrowAny {
            permutations {
               assume { error("boom") }
            }
         }
      }

      test("assume(fn) should support assertions") {
         permutations {

            val a by gen { Arb.string(2, Codepoint.az()) }
            val b by gen { Arb.string(2, Codepoint.az()) }

            assume {
               a shouldNotBe b
               a shouldHaveLength b.length
            }

            a.compareTo(b) shouldNotBe 0
         }
      }

      test("assumptions should fail if too many are discarded") {
         // this will throw because we only have 9 combinations and 3 are disallowed
         shouldThrow<MaxDiscardPercentageException> {
            permutations {
               val a by gen { Arb.int(0..2) }
               val b by gen { Arb.int(0..2) }
               assume(a != b)
            }
         }
      }

      test("assumptions should honour maxDiscardPercentage config") {
         // this will pass because we upped the discard percentage
         shouldThrow<MaxDiscardPercentageException> {
            permutations {
               maxDiscardPercentage = 50
               val a by gen { Arb.int(0..2) }
               val b by gen { Arb.int(0..2) }
               assume(a != b)
            }
         }
      }

      test("discards counter") {

         val result1 = permutations {
            val a by gen { Arb.constant("a") }
            val b by gen { Arb.constant("b") }
            assume(a != b)
         }
         result1.discards.shouldBe(0)

         val result2 = permutations {
            val a by gen { Arb.string(1, Codepoint.az()) }
            val b by gen { Arb.constant("b") }
            assume(a != b)
         }
         result2.discards.shouldBe(3)
      }

      test("discards should not count towards iteration counts") {

         val result1 = permutations {
            val a by gen { Exhaustive.of(1, 2, 3) }
            assume(a < 3)
         }
         result1.iterations.shouldBe(1)

         val result2 = permutations {
            val a by gen { Exhaustive.of(1, 2, 3, 4) }
            assume(a < 3)
         }
         result2.iterations.shouldBe(3)
      }
   }
}
