package io.kotest.permutations

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.int
import io.kotest.property.exhaustive.ints

class DelegateTest : FunSpec() {
   init {

      test("gen values should be consistent during the same iteration") {
         permutations {

            val a by gen { Arb.int() }
            val b by gen { Exhaustive.ints(0..10) }

            forEach {
               a + b shouldBe a + b
               a + b shouldBe a + b
               a + b shouldBe a + b
            }
         }
      }

      test("gen values should rotate during different iterations") {
         val values = mutableSetOf<Int>()
         permutations {
            iterations = 5
            val a by gen { Exhaustive.ints(1..5) }
            forEach {
               values.add(a)
            }
         }
         values shouldBe setOf(1, 2, 3, 4, 5)
      }
   }
}


