package com.sksamuel.kotest.specs.freespec

import io.kotest.IsolationMode
import io.kotest.shouldBe
import io.kotest.specs.FreeSpec
import java.util.concurrent.atomic.AtomicInteger

class FreeSpecInstancePerNodeTest : FreeSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  val count = AtomicInteger(0)

  init {
    "1" - {
      count.incrementAndGet().shouldBe(1)
      "1.1" {
        count.incrementAndGet().shouldBe(2)
      }
      "1.2" - {
        count.incrementAndGet().shouldBe(2)
        "1.2.1" {
          count.incrementAndGet().shouldBe(3)
        }
        "1.2.2" - {
          count.incrementAndGet().shouldBe(3)
          "1.2.2.1" {
            count.incrementAndGet().shouldBe(4)
          }
          "1.2.2.2" {
            count.incrementAndGet().shouldBe(4)
          }
        }
      }
    }
    "2" - {
      count.incrementAndGet().shouldBe(1)
      "2.1" - {
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