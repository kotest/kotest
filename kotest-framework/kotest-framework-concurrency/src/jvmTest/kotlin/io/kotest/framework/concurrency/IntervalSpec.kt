package io.kotest.framework.concurrency

import io.kotest.assertions.all
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.math.pow
import kotlin.time.milliseconds
import kotlin.time.minutes
import kotlin.time.seconds

@OptIn(ExperimentalKotest::class)
class IntervalSpec : FunSpec({
   context("fixed interval") {
      test("always returns the same value") {
         val expected = 25L
         val interval = expected.fixed()

         (0..100).forEach { interval.next(it) shouldBe 25L }
      }
   }

   context("exponential interval") {
      test("has a reasonable default next") {
         val identity = 2.seconds.toLongMilliseconds()

         all(identity.exponential()) {
            next(0) shouldBe identity * 1
            next(1) shouldBe identity * 2
            next(2) shouldBe identity * 4
         }

         all(identity.exponential(factor = 3.0)) {
            next(0) shouldBe identity * 1
            next(1) shouldBe identity * 3
            next(2) shouldBe identity * 9
         }
      }

      test("has a reasonable default max") {
         val max = ExponentialInterval.defaultMax
         val default = 25.milliseconds.toLongMilliseconds().exponential()
         val unbounded = 25.milliseconds.toLongMilliseconds().exponential(max = null)

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

      test("respects user specified max") {
         val base = 25.milliseconds.toLongMilliseconds()
         val n = 5
         val max = base * ExponentialInterval.defaultFactor.pow(n).toLong()
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

   context("fibonacci interval") {
      test("correctness") {
         fibonacci(0) shouldBe 0
         fibonacci(1) shouldBe 1
         fibonacci(2) shouldBe 1
         fibonacci(3) shouldBe 2
         fibonacci(4) shouldBe 3
         fibonacci(5) shouldBe 5
         fibonacci(6) shouldBe 8
         fibonacci(7) shouldBe 13
      }

      test("has a reasonable default max") {
         val max = FibonacciInterval.defaultMax
         val default = 10.minutes.toLongMilliseconds().fibonacci()
         val unbounded = 10.minutes.toLongMilliseconds().fibonacci(null)

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

      test("respects user specified max") {
         val max = FibonacciInterval.defaultMax + 15.minutes.toLongMilliseconds()
         val bounded = 10.minutes.toLongMilliseconds().fibonacci(max)
         val unbounded = 10.minutes.toLongMilliseconds().fibonacci(null)

         val first = 0
         val last = 20

         unbounded.next(first) shouldBeLessThan max
         unbounded.next(last) shouldBeGreaterThan max

         for (i in first..last) {
            val u = unbounded.next(i)
            val b = bounded.next(i)

            if (u < max) {
               b shouldBe u
            } else {
               b shouldBe max
               u shouldBeGreaterThan max
            }
         }
      }
   }
})
