package com.sksamuel.kotlintest.await

import io.kotlintest.await.await
import io.kotlintest.await.awaitListener
import io.kotlintest.eventually.fixedInterval
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AwaitTest : FunSpec({

  test("await with boolean predicate") {
    await(Duration.ofSeconds(5)) {
      System.currentTimeMillis() > 0
    }
  }

  test("await with boolean predicate and interval") {
    await(Duration.ofSeconds(5), fixedInterval(Duration.ofSeconds(1))) {
      System.currentTimeMillis() > 0
    }
  }

  test("await with T predicate") {
    var t = ""
    await(Duration.ofSeconds(5), { t == "xxxx" }) {
      t += "x"
    }
  }

  test("await with T predicate and interval") {
    var t = ""
    val result = await(Duration.ofSeconds(5), fixedInterval(Duration.ofMillis(250)), { t == "xxxxxxxxxxx" }) {
      t += "x"
      t
    }
    result shouldBe "xxxxxxxxxxx"
  }

  test("await with T predicate, interval, and listener") {
    var t = ""
    val latch = CountDownLatch(5)
    val listener = awaitListener<String> { latch.countDown() }
    val result = await(Duration.ofSeconds(5), fixedInterval(Duration.ofMillis(250)), { t == "xxxxxxxxxxx" }, listener) {
      t += "x"
      t
    }
    latch.await(15, TimeUnit.SECONDS) shouldBe true
    result shouldBe "xxxxxxxxxxx"
  }
})