@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

@OptIn(ExperimentalKotest::class)
class MissingCheckTest : FunSpec() {
   init {

      test("permutations should throw if no check block is provided") {
         val ex = shouldThrow<IllegalStateException> {
            permutations {
               iterations = 5
            }
         }
         ex.message shouldBe "test has not been set"
      }

      test("permutations should throw if generators are registered but no check block is provided") {
         val ex = shouldThrow<IllegalStateException> {
            permutations {
               iterations = 5
               val a by gen { Arb.int(0..10) }
            }
         }
         ex.message shouldBe "test has not been set"
      }
   }
}
