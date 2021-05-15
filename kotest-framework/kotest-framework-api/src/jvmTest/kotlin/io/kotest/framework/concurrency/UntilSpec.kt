package io.kotest.framework.concurrency

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

private fun Int.seconds(): Millis = Duration.ofSeconds(this.toLong()).toMillis()
private fun Int.milliseconds(): Millis = this.toLong()

@OptIn(ExperimentalKotest::class)
class UntilSpec : FunSpec({
   test("until with immediate boolean predicate") {
      var attempts = 0
      until(1.seconds()) {
         attempts++
         System.currentTimeMillis() > 0
      }
      attempts shouldBe 1
   }

   test("until with boolean predicate that resolves before time duration") {
      var attempts = 0
      until(3.seconds()) {
         attempts++
         attempts == 2
      }
      attempts shouldBe 2
   }

   test("until with boolean predicate and interval") {
      var attempts = 0
      until(2.seconds(), 10.milliseconds().fixed()) {
         attempts++
         attempts == 100
      }
      attempts shouldBe 100
   }

   test("until with patience config") {
      var attempts = 0
      until(PatienceConfig(2.seconds(), 10.milliseconds().fixed())) {
         attempts++
         attempts == 100
      }
      attempts shouldBe 100
   }

   test("until with predicate") {
      var attempts = 0
      var t = ""
      until(5.seconds(), listener = { t == "xxx" }) {
         attempts++
         t += "x"
      }
      attempts shouldBe 3
   }

   test("until with predicate and interval") {
      measureTimeMillis {
         var attempts = 0
         var t = ""
         until(1.seconds(), 10.milliseconds().fixed(), { t == "xxxx" }) {
            attempts++
            t += "x"
         }
         attempts shouldBe 4
      }.shouldBeLessThan(100)
   }

   test("until with predicate, interval, and listener") {
      var t = ""
      val latch = CountDownLatch(5)
      val result = until(1.seconds(), 10.milliseconds().fixed(), listener = { latch.countDown(); t == "xxxxx" }) {
         t += "x"
         t
      }
      latch.await(15, TimeUnit.SECONDS) shouldBe true
      result shouldBe "xxxxx"
   }

   test("until should throw when the predicate doesn't equal true in the time period") {
      shouldThrow<AssertionError> {
         until(1.seconds(), listener = { it.result == 2 }) {
            1
         }
      }
   }

   test("until should support fibonacci intervals") {
      measureTimeMillis {
         var t = ""
         var attempts = 0
         val result = until(10.seconds(), 10.milliseconds().fibonacci(), { t == "xxxxxx" }) {
            attempts++
            t += "x"
            t
         }
         attempts shouldBe 6
         result shouldBe "xxxxxx"
      }.shouldBeGreaterThan(100)
   }

})
