package com.sksamuel.kotest.specs.wordspec

import io.kotest.IsolationMode
import io.kotest.shouldBe
import io.kotest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

class WordSpecOneInstanceTest : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

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
