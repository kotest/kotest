package com.sksamuel.kotest.assertions.until

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.until.ExponentialInterval
import io.kotest.assertions.until.exponential
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.math.pow
import kotlin.time.milliseconds
import kotlin.time.seconds

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

      test("exponential interval should have a reasonable default cap") {
         val cap = ExponentialInterval.defaultCap
         val default = 25.milliseconds.exponential()
         val unbounded = 25.milliseconds.exponential(cap = null)

         val first = 0
         val last = 20

         unbounded.next(first) shouldBeLessThan cap
         unbounded.next(last) shouldBeGreaterThan cap

         for (i in first..last) {
            val u = unbounded.next(i)
            val d = default.next(i)

            if (u < cap) {
               d shouldBe u
            } else {
               d shouldBe cap
               u shouldBeGreaterThan cap
            }
         }
      }

      test("exponential interval should respect user specified cap") {
         val base = 25.milliseconds
         val n = 5
         val cap = base * ExponentialInterval.defaultFactor.pow(n)
         val bounded = base.exponential(cap = cap)
         val unbounded = base.exponential(cap = null)

         val first = 0
         val last = 20

         unbounded.next(first) shouldBeLessThan cap
         unbounded.next(last) shouldBeGreaterThan cap

         for (i in first..last) {
            val u = unbounded.next(i)
            val b = bounded.next(i)

            if (u < cap) {
               b shouldBe u
               i shouldBeLessThan n
            } else {
               i shouldBeGreaterThanOrEqualTo n
               b shouldBe cap
               u shouldBeGreaterThanOrEqualTo cap
            }
         }
      }
   }
}
