package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import java.util.concurrent.atomic.AtomicInteger

class FreeSpecOneInstanceTest : FreeSpec() {

  override fun isInstancePerTest(): Boolean = true

  val count = AtomicInteger(0)

  init {
    "1" {
      count.incrementAndGet().shouldBe(1)
      "1.1" {
        count.incrementAndGet().shouldBe(2)
      }
      "1.2" {
        count.incrementAndGet().shouldBe(2)
        "1.2.1" {
          count.incrementAndGet().shouldBe(3)
        }
        "1.2.2" {
          count.incrementAndGet().shouldBe(3)
          "1.2.2.1" {
            count.incrementAndGet().shouldBe(12)
          }
          "1.2.2.2" {
            count.incrementAndGet().shouldBe(4)
          }
        }
      }
    }
    "2" {
      count.incrementAndGet().shouldBe(1)
      "2.1" {
        count.incrementAndGet().shouldBe(2)
        "2.1.1" {
          count.incrementAndGet().shouldBe(3)
        }
        "2.1.2" {
          count.incrementAndGet().shouldBe(3)
        }
      }
    }
  }
}