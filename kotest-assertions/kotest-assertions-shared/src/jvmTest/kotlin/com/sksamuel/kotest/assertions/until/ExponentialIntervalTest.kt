package com.sksamuel.kotest.assertions.until

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.until.ExponentialInterval
import io.kotest.assertions.until.exponential
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.time.hours
import kotlin.time.milliseconds
import kotlin.time.seconds

class ExponentialIntervalTest : FunSpec() {
   init {
      test("exponential interval should have a reasonable default next") {
         val identity = 2.seconds

         assertSoftly(identity.exponential()) {
            next(0) shouldBe identity
            next(1) shouldBe 4.seconds
            next(2) shouldBe 8.seconds
         }

         assertSoftly(identity.exponential(factor = 3.0)) {
            next(0) shouldBe identity
            next(1) shouldBe 6.seconds
            next(2) shouldBe 9.seconds
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
         val cap = 278.hours
         val bounded = 25.milliseconds.exponential(cap = cap)
         val unbounded = 25.milliseconds.exponential(cap = null)

         val first = 0
         val last = 20

         unbounded.next(first) shouldBeLessThan cap
         unbounded.next(last) shouldBeGreaterThan cap

         for (i in first..last) {
            val u = unbounded.next(i)
            val b = bounded.next(i)

            if (u < cap) {
               b shouldBe u
            } else {
               b shouldBe cap
               u shouldBeGreaterThan cap
            }
         }
      }
   }
}
