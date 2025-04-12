package io.kotest.permutations.checks

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.permutations.permutations
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.ints

@EnabledIf(LinuxOnlyGithubCondition::class)
class MaxFailureTest : FunSpec({

   test("test should fail once the failure rate is higher than maxFailure setting") {
      val message = shouldThrowAny {
         permutations {
            seed = 1900646515
            maxFailures = 5
            val a by gen { Exhaustive.ints(0..10) }
            forEach {
               a shouldBeLessThan 8
            }
         }
      }.message
      message shouldContain """Property failed after 22 attempts"""
      message shouldContain """Max failures was 5"""
   }
})
