package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

class WordSpecOneInstanceTest : WordSpec() {

  override fun isInstancePerTest(): Boolean = true

  val count = AtomicInteger(0)

  init {
    "1" should {
      count.incrementAndGet().shouldBe(1)
      "1.1" {
        count.incrementAndGet().shouldBe(2)
      }
      "1.2" {
        count.incrementAndGet().shouldBe(2)
      }
    }
    "2" should {
      count.incrementAndGet().shouldBe(1)
      "2.1" {
        count.incrementAndGet().shouldBe(2)
      }
      "2.2" {
        count.incrementAndGet().shouldBe(2)
      }
    }
  }
}