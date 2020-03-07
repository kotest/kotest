package com.sksamuel.kotest.assertions.until

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.until.fibonacci
import io.kotest.assertions.until.fixed
import io.kotest.assertions.until.until
import io.kotest.assertions.until.untilListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

@OptIn(ExperimentalTime::class)
class UntilTest : FunSpec({

   test("until with boolean predicate") {
      until(5.seconds) {
         System.currentTimeMillis() > 0
      }
   }

   test("until with boolean predicate and interval") {
      until(5.seconds, 1.seconds.fixed()) {
         System.currentTimeMillis() > 0
      }
   }

   test("until with T predicate") {
      var t = ""
      until(5.seconds, { t == "xxxx" }) {
         t += "x"
      }
   }

   test("until with T predicate and interval") {
      var t = ""
      val result = until(5.seconds, 250.milliseconds.fixed(), { t == "xxxxxxxxxxx" }) {
         t += "x"
         t
      }
      result shouldBe "xxxxxxxxxxx"
   }

   test("until with T predicate, interval, and listener") {
      var t = ""
      val latch = CountDownLatch(5)
      val listener = untilListener<String> { latch.countDown() }
      val result = until(5.seconds, 250.milliseconds.fixed(), { t == "xxxxxxxxxxx" }, listener) {
         t += "x"
         t
      }
      latch.await(15, TimeUnit.SECONDS) shouldBe true
      result shouldBe "xxxxxxxxxxx"
   }

   test("fail tests that fail a predicate") {
      shouldThrow<AssertionError> {
         until(1.seconds, { it == 2 }) {
            1
         }
      }
   }

   test("support fibonacci intervals") {
      var t = ""
      val latch = CountDownLatch(5)
      val listener = untilListener<String> { latch.countDown() }
      val result = until(10.seconds, 200.milliseconds.fibonacci(), { t == "xxxxxx" }, listener) {
         t += "x"
         t
      }
      latch.await(10, TimeUnit.SECONDS) shouldBe true
      result shouldBe "xxxxxx"
   }
})
