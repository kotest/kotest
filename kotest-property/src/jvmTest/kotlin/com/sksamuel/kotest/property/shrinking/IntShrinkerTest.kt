package com.sksamuel.kotest.property.shrinking

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.IntShrinker

@EnabledIf(LinuxOnlyGithubCondition::class)
class IntShrinkerTest : WordSpec() {

   override suspend fun afterSpec(spec: Spec) {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

   override suspend fun beforeSpec(spec: Spec) {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   init {
      "IntShrinker" should {
         val shrinker = IntShrinker(Int.MIN_VALUE..Int.MAX_VALUE)
         "return empty list for zero" {
            shrinker.shrink(0).shouldBeEmpty()
         }
         "include zero for 1 or -1" {
            shrinker.shrink(1).shouldHaveSingleElement(0)
            shrinker.shrink(-1).shouldHaveSingleElement(0)
         }
         "include zero as the first candidate" {
            shrinker.shrink(55).shouldHaveElementAt(0, 0)
         }
         "include fiver smaller elements" {
            shrinker.shrink(55).shouldContainAll(50, 51, 52, 53, 54)
         }
         "include fiver smaller elements unless smaller than zero" {
            shrinker.shrink(2).shouldNotContain(-2)
         }
         "include abs value for negative" {
            shrinker.shrink(-55).shouldContain(55)
            shrinker.shrink(55).shouldNotContain(55)
         }
         "include 1 and negative 1" {
            val candidates = shrinker.shrink(56)
            candidates.shouldContainAll(1, -1)
         }
         "include 1/3" {
            val candidates = shrinker.shrink(90)
            candidates.shouldContain(30)
         }
         "include 1/2" {
            val candidates = shrinker.shrink(90)
            candidates.shouldContain(45)
         }
         "include 2/3" {
            val candidates = shrinker.shrink(90)
            candidates.shouldContain(60)
         }
         // regression: subtracting 1..5 from a value near Int.MIN_VALUE underflowed silently
         // and the wrapped-positive results then survived the `> 0` filter, so the shrinker
         // proposed values ~Int.MAX_VALUE as "shrinks" of values ~Int.MIN_VALUE.
         // regression: subtracting 1..5 from a value near Int.MIN_VALUE underflowed silently
         // and the wrapped-positive results then survived the `> 0` filter, so the shrinker
         // proposed values ~Int.MAX_VALUE as "shrinks" of values ~Int.MIN_VALUE.
         "not propose subtract-underflowed candidates when shrinking values near Int.MIN_VALUE" {
            // Specifically check the wrapped values that were the bug. abs(value) is intended
            // to appear and is allowed (it's a legitimate "flip the sign" shrink candidate).
            //
            // For value = Int.MIN_VALUE+4, the buggy code computed `value - 5 = Int.MIN_VALUE-1`
            // which wrapped to Int.MAX_VALUE, then survived the `it > 0` filter. abs(value)
            // for the same input is Int.MAX_VALUE - 3, so we can disambiguate.
            shrinker.shrink(Int.MIN_VALUE + 4).shouldNotContain(Int.MAX_VALUE)
            shrinker.shrink(Int.MIN_VALUE + 3).shouldNotContain(Int.MAX_VALUE)
            shrinker.shrink(Int.MIN_VALUE + 3).shouldNotContain(Int.MAX_VALUE - 1)
         }
      }
   }
}
