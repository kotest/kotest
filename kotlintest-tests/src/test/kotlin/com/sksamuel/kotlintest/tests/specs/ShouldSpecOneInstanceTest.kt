package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import java.util.concurrent.atomic.AtomicInteger

class ShouldSpecOneInstanceTest : ShouldSpec() {

  override fun isInstancePerTest(): Boolean = true

  val count = AtomicInteger(0)

  init {
    "1" {
      count.incrementAndGet().shouldBe(1)
      should("1.1") {
        count.incrementAndGet().shouldBe(2)
      }
      "1.2" {
        count.incrementAndGet().shouldBe(2)
        should("1.2.1") {
          count.incrementAndGet().shouldBe(3)
        }
        "1.2.2" {
          count.incrementAndGet().shouldBe(3)
          should("1.2.2.1") {
            count.incrementAndGet().shouldBe(4)
          }
          should("1.2.2.2") {
            count.incrementAndGet().shouldBe(4)
          }
        }
      }
    }
    "2" {
      count.incrementAndGet().shouldBe(1)
      "2.1" {
        count.incrementAndGet().shouldBe(2)
        should("2.1.1") {
          count.incrementAndGet().shouldBe(3)
        }
        should("2.1.2") {
          count.incrementAndGet().shouldBe(3)
        }
      }
    }
  }
}