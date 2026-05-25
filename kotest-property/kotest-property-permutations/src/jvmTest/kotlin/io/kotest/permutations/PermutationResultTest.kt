@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.exhaustive.char
import io.kotest.property.exhaustive.of

@OptIn(ExperimentalKotest::class)
@EnabledIf(LinuxOnlyGithubCondition::class)
class PermutationResultTest : FunSpec() {
   init {

      test("PermutationResult should contain invocations count including discards") {
         // 26 letters generated against the single letter 'b' produces 25 discards + 1 attempt;
         // invocations count every entry into the check function, so should be 26.
         val result = permutations {
            iterations = 26
            val a by gen { Exhaustive.char('a'..'z') }
            val b by gen { Exhaustive.of('b') }
            check {
               assume(a == b)
            }
         }
         result.invocations shouldBe 26
      }

      test("PermutationResult should contain attempts count which excludes discards") {
         // 26 letters generated against 'b' → 1 non-discarded attempt, 25 discards.
         val result = permutations {
            iterations = 26
            val a by gen { Exhaustive.char('a'..'z') }
            val b by gen { Exhaustive.of('b') }
            check {
               assume(a == b)
            }
         }
         result.attempts shouldBe 1
         result.discards shouldBe 25
      }

      test("PermutationResult should contain discards counts") {
         val result = permutations {
            iterations = 26
            val a by gen { Exhaustive.char('a'..'z') }
            val b by gen { Exhaustive.of('b') }
            check {
               assume(a == b)
            }
         }
         result.discards shouldBe 25
      }

      test("PermutationResult should contain success counts") {
         val result = permutations {
            iterations = 7
            val a by gen { Arb.constant(1) }
            check {
               a shouldBe 1
            }
         }
         result.successes shouldBe 7
         result.failures shouldBe 0
      }

      test("PermutationResult should contain failure counts") {
         // tolerate failures and require no successes so the run completes and returns a result.
         val result = permutations {
            iterations = 4
            maxFailures = Int.MAX_VALUE
            minSuccess = 0
            val a by gen { Arb.constant(1) }
            check {
               a shouldBe 2
            }
         }
         result.failures shouldBe 4
         result.successes shouldBe 0
      }

      test("PermutationResult should contain duration") {
         val result = permutations {
            iterations = 5
            val a by gen { Arb.int(0..10) }
            check {
               a shouldBe a
            }
         }
         result.duration.inWholeNanoseconds shouldBeGreaterThan 0L
      }
   }
}
