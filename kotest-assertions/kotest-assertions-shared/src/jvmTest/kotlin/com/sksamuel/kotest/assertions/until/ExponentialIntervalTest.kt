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
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.seconds

class ExponentialIntervalTest : FunSpec() {
   init {
      test("exponential interval should have a reasonable default next") {
         val identity = Duration.seconds(2)

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
          val max = ExponentialInterval.defaultMax
          val default = Duration.milliseconds(25).exponential()
          val unbounded = Duration.milliseconds(25).exponential(max = null)

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
          val base = Duration.milliseconds(25)
          val n = 5
          val max = base * ExponentialInterval.defaultFactor.pow(n)
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
