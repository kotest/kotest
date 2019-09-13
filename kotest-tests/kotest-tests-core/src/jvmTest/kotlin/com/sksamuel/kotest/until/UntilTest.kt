package com.sksamuel.kotest.until

import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.FunSpec
import io.kotest.until.fibonacciInterval
import io.kotest.until.fixedInterval
import io.kotest.until.until
import io.kotest.until.untilListener
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UntilTest : FunSpec({

  test("until with boolean predicate") {
    until(Duration.ofSeconds(5)) {
      System.currentTimeMillis() > 0
    }
  }

  test("until with boolean predicate and interval") {
    until(Duration.ofSeconds(5), fixedInterval(Duration.ofSeconds(1))) {
      System.currentTimeMillis() > 0
    }
  }

  test("until with T predicate") {
    var t = ""
    until(Duration.ofSeconds(5), { t == "xxxx" }) {
      t += "x"
    }
  }

  test("until with T predicate and interval") {
    var t = ""
    val result = until(Duration.ofSeconds(5), fixedInterval(Duration.ofMillis(250)), { t == "xxxxxxxxxxx" }) {
      t += "x"
      t
    }
    result shouldBe "xxxxxxxxxxx"
  }

  test("until with T predicate, interval, and listener") {
    var t = ""
    val latch = CountDownLatch(5)
    val listener = untilListener<String> { latch.countDown() }
    val result = until(Duration.ofSeconds(5),
        fixedInterval(Duration.ofMillis(250)), { t == "xxxxxxxxxxx" }, listener) {
      t += "x"
      t
    }
    latch.await(15, TimeUnit.SECONDS) shouldBe true
    result shouldBe "xxxxxxxxxxx"
  }

  test("fail tests that fail a predicate") {
    shouldThrow<AssertionError> {
      until(Duration.ofSeconds(1), { it == 2 }) {
        1
      }
    }
  }

  test("support fibonacci intervals") {
    var t = ""
    val latch = CountDownLatch(5)
    val listener = untilListener<String> { latch.countDown() }
    val result = until(Duration.ofSeconds(10),
        fibonacciInterval(Duration.ofMillis(200)), { t == "xxxxxx" }, listener) {
      t += "x"
      t
    }
    latch.await(10, TimeUnit.SECONDS) shouldBe true
    result shouldBe "xxxxxx"
  }
})