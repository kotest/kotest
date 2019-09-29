package com.sksamuel.kotest.specs.behavior

import io.kotest.IsolationMode
import io.kotest.shouldBe
import io.kotest.specs.BehaviorSpec
import java.util.concurrent.atomic.AtomicInteger

class BehaviorSpecOneInstanceTest : BehaviorSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  val count = AtomicInteger(0)

  init {
    Given("1") {
      count.incrementAndGet().shouldBe(1)
      When("1.1") {
        count.incrementAndGet().shouldBe(2)
      }
      When("1.2") {
        count.incrementAndGet().shouldBe(2)
        Then("1.2.1") {
          count.incrementAndGet().shouldBe(3)
        }
        Then("1.2.2") {
          count.incrementAndGet().shouldBe(3)
        }
      }
    }
    Given("2") {
      count.incrementAndGet().shouldBe(1)
      When("2.1") {
        count.incrementAndGet().shouldBe(2)
        Then("2.1.1") {
          count.incrementAndGet().shouldBe(3)
        }
        Then("2.1.2") {
          count.incrementAndGet().shouldBe(3)
        }
      }
    }
  }
}
