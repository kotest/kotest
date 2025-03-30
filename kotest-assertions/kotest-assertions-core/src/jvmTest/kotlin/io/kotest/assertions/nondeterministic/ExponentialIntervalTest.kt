package io.kotest.assertions.nondeterministic

import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@EnabledIf(NotMacOnGithubCondition::class)
class ExponentialIntervalTest : FunSpec() {
   init {
      test("exponential interval should have a reasonable default next") {
         val identity = 2.seconds

         assertSoftly(identity.exponential()) {
            next(0) shouldBe identity * 1
            next(1) shouldBe identity * 2
            next(2) shouldBe identity * 4
         }

         assertSoftly(identity.exponential(factor = 3.0)) {
            next(0) shouldBe identity * 1
            next(1) shouldBe identity * 3
            next(2) shouldBe identity * 9
         }
      }

      test("exponential interval should have a reasonable default max") {
         val max = ExponentialIntervalFn.defaultMax
         val default = 25.milliseconds.exponential()
         val unbounded = 25.milliseconds.exponential(max = null)

         val first = 0
         val last = 20

         unbounded.next(first) shouldBeLessThan max
         unbounded.next(last) shouldBeGreaterThan max

         for (i in first..last) {
            val u = unbounded.next(i)
            val d = default.next(i)

            if (u < max) {
               d shouldBe u
            } else {
               d shouldBe max
               u shouldBeGreaterThan max
            }
         }
      }

      test("exponential interval should respect user specified max") {
         val base = 25.milliseconds
         val n = 5
         val max = base * ExponentialIntervalFn.defaultFactor.pow(n)
         val bounded = base.exponential(max = max)
         val unbounded = base.exponential(max = null)

         val first = 0
         val last = 20

         unbounded.next(first) shouldBeLessThan max
         unbounded.next(last) shouldBeGreaterThan max

         for (i in first..last) {
            val u = unbounded.next(i)
            val b = bounded.next(i)

            if (u < max) {
               b shouldBe u
               i shouldBeLessThan n
            } else {
               i shouldBeGreaterThanOrEqualTo n
               b shouldBe max
               u shouldBeGreaterThanOrEqualTo max
            }
         }
      }
   }
}
