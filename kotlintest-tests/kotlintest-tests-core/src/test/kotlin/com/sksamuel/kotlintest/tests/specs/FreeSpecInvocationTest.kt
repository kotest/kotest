package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import java.util.concurrent.atomic.AtomicInteger

class FreeSpecInvocationTest : FreeSpec({

  val count = AtomicInteger(0)

  "a" {
    count.get().shouldBe(0)
    "b".config(invocations = 5) {
      count.incrementAndGet()
    }
    count.get().shouldBe(5)
    count.incrementAndGet()
    count.get().shouldBe(6)
    "c" {
      count.get().shouldBe(6)
      count.incrementAndGet()
      count.get().shouldBe(7)
      "d".config(invocations = 3) {
        count.incrementAndGet()
      }
      count.get().shouldBe(10)
      count.incrementAndGet()
      count.get().shouldBe(11)
      "e" {
        count.get().shouldBe(11)
        count.incrementAndGet()
        count.get().shouldBe(12)
      }
    }
    count.get().shouldBe(12)
    count.incrementAndGet()
    count.get().shouldBe(13)
    "f" {
      count.get().shouldBe(13)
      count.incrementAndGet()
      count.get().shouldBe(14)
    }
  }

  "g" {
    count.get().shouldBe(14)
    count.incrementAndGet()
    count.get().shouldBe(15)
    "h".config(invocations = 8) {
      count.incrementAndGet()
    }
    count.get().shouldBe(23)
    count.incrementAndGet()
    count.get().shouldBe(24)
  }
})