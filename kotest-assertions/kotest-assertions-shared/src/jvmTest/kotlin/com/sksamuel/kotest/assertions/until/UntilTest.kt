package com.sksamuel.kotest.assertions.until

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.until.PatienceConfig
import io.kotest.assertions.until.fibonacci
import io.kotest.assertions.until.fixed
import io.kotest.assertions.until.until
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.time.TimeSource
import kotlin.time.milliseconds
import kotlin.time.seconds

class UntilTest : FunSpec({

   test("until with immediate boolean predicate") {
      var attempts = 0
      until(1.seconds) {
         attempts++
         System.currentTimeMillis() > 0
      }
      attempts shouldBe 1
   }

   test("until with boolean predicate that resolves before time duration") {
      var attempts = 0
      until(3.seconds) {
         attempts++
         attempts == 2
      }
      attempts shouldBe 2
   }

   test("until with boolean predicate and interval") {
      var attempts = 0
      until(2.seconds, 10.milliseconds.fixed()) {
         attempts++
         attempts == 100
      }
      attempts shouldBe 100
   }

   test("until with patience config") {
      var attempts = 0
      until(PatienceConfig(2.seconds, 10.milliseconds.fixed())) {
         attempts++
         attempts == 100
      }
      attempts shouldBe 100
   }

   test("until with predicate") {
      var attempts = 0
      var t = ""
      until(5.seconds, { t == "xxx" }) {
         attempts++
         t += "x"
      }
      attempts shouldBe 3
   }

   test("until with predicate and interval") {
      val start = TimeSource.Monotonic.markNow()
      var attempts = 0
      var t = ""
      until(1.seconds, 10.milliseconds.fixed(), { t == "xxxx" }) {
         attempts++
         t += "x"
      }
      attempts shouldBe 4
      start.elapsedNow().shouldBeLessThan(100.milliseconds)
   }

   test("until with predicate, interval, and listener") {
      var t = ""
      val latch = CountDownLatch(5)
      val result = until(1.seconds, 10.milliseconds.fixed(), { t == "xxxxx" }, { latch.countDown() }) {
         t += "x"
         t
      }
      latch.await(15, TimeUnit.SECONDS) shouldBe true
      result shouldBe "xxxxx"
   }

   test("until should throw when the predicate doesn't equal true in the time period") {
      shouldThrow<AssertionError> {
         until(1.seconds, { it == 2 }) {
            1
         }
      }
   }

   test("until should support fibonacci intervals") {
      val start = TimeSource.Monotonic.markNow()
      var t = ""
      var attempts = 0
      val result = until(10.seconds, 10.milliseconds.fibonacci(), { t == "xxxxxx" }) {
         attempts++
         t += "x"
         t
      }
      attempts shouldBe 6
      result shouldBe "xxxxxx"
      start.elapsedNow().toLongMilliseconds().shouldBeGreaterThan(100)
   }
})
