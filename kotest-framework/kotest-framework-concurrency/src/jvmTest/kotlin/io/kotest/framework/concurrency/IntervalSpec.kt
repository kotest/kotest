package io.kotest.framework.concurrency

import io.kotest.assertions.all
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.math.pow
import kotlin.time.Duration

@ExperimentalKotest
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
         val identity = Duration.seconds(2).inWholeMilliseconds

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
         val default = Duration.milliseconds(25).inWholeMilliseconds.exponential()
         val unbounded = Duration.milliseconds(25).inWholeMilliseconds.exponential(max = null)

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
         val base = Duration.milliseconds(25).inWholeMilliseconds
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
         val default = Duration.minutes(10).inWholeMilliseconds.fibonacci()
         val unbounded = Duration.minutes(10).inWholeMilliseconds.fibonacci(null)

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
         val max = FibonacciInterval.defaultMax + Duration.minutes(15).inWholeMilliseconds
         val bounded = Duration.minutes(10).inWholeMilliseconds.fibonacci(max)
         val unbounded = Duration.minutes(10).inWholeMilliseconds.fibonacci(null)

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
