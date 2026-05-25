@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalKotest::class)
class FailOnSeedTest : FunSpec() {
   init {
      test("property test should fail if seed is specified when failOnSeed is true") {
         shouldThrowAny {
            permutations {
               failOnSeed = true
               seed = 1234
               check {
                  1 shouldBe 1
               }
            }
         }.message shouldBe """A seed is specified on this permutation but failOnSeed is true"""
      }
   }
}
