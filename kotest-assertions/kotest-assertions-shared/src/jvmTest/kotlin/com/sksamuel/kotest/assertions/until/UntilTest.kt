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
import kotlin.time.Duration
import kotlin.time.TimeSource

class UntilTest : FunSpec({

   test("until with immediate boolean predicate") {
      var attempts = 0
       until(Duration.seconds(1)) {
           attempts++
           System.currentTimeMillis() > 0
       }
      attempts shouldBe 1
   }

   test("until with boolean predicate that resolves before time duration") {
      var attempts = 0
       until(Duration.seconds(3)) {
           attempts++
           attempts == 2
       }
      attempts shouldBe 2
   }

   test("until with boolean predicate and interval") {
      var attempts = 0
       until(Duration.seconds(2), Duration.milliseconds(10).fixed()) {
           attempts++
           attempts == 100
       }
      attempts shouldBe 100
   }

   test("until with patience config") {
      var attempts = 0
      until(PatienceConfig(Duration.seconds(2), Duration.milliseconds(10).fixed())) {
         attempts++
         attempts == 100
      }
      attempts shouldBe 100
   }

   test("until with predicate") {
      var attempts = 0
      var t = ""
       until(Duration.seconds(5), { t == "xxx" }) {
           attempts++
           t += "x"
       }
      attempts shouldBe 3
   }

   test("until with predicate and interval") {
      val start = TimeSource.Monotonic.markNow()
      var attempts = 0
      var t = ""
       until(Duration.seconds(1), Duration.milliseconds(10).fixed(), { t == "xxxx" }) {
           attempts++
           t += "x"
       }
      attempts shouldBe 4
      start.elapsedNow().shouldBeLessThan(Duration.milliseconds(100))
   }

   test("until with predicate, interval, and listener") {
      var t = ""
      val latch = CountDownLatch(5)
      val result =
          until(Duration.seconds(1), Duration.milliseconds(10).fixed(), { t == "xxxxx" }, { latch.countDown() }) {
              t += "x"
              t
          }
      latch.await(15, TimeUnit.SECONDS) shouldBe true
      result shouldBe "xxxxx"
   }

   test("until should throw when the predicate doesn't equal true in the time period") {
      shouldThrow<AssertionError> {
          until(Duration.seconds(1), { it == 2 }) {
              1
          }
      }
   }

   test("until should support fibonacci intervals") {
      val start = TimeSource.Monotonic.markNow()
      var t = ""
      var attempts = 0
      val result = until(Duration.seconds(10), Duration.milliseconds(10).fibonacci(), { t == "xxxxxx" }) {
          attempts++
          t += "x"
          t
      }
      attempts shouldBe 6
      result shouldBe "xxxxxx"
      start.elapsedNow().inWholeMilliseconds.shouldBeGreaterThan(100)
   }
})
