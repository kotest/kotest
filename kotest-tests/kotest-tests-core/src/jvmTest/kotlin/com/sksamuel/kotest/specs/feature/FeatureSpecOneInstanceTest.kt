package com.sksamuel.kotest.specs.feature

import io.kotest.IsolationMode
import io.kotest.shouldBe
import io.kotest.specs.FeatureSpec
import java.util.concurrent.atomic.AtomicInteger

class FeatureSpecOneInstanceTest : FeatureSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  val count = AtomicInteger(0)

  init {
    feature("1") {
      count.incrementAndGet().shouldBe(1)
      scenario("1.1") {
        count.incrementAndGet().shouldBe(2)
      }
      and("1.2") {
        count.incrementAndGet().shouldBe(2)
        scenario("1.2.1") {
          count.incrementAndGet().shouldBe(3)
        }
        and("1.2.2") {
          count.incrementAndGet().shouldBe(3)
          scenario("1.2.2.1") {
            count.incrementAndGet().shouldBe(4)
          }
          scenario("1.2.2.2") {
            count.incrementAndGet().shouldBe(4)
          }
        }
      }
    }
    feature("2") {
      count.incrementAndGet().shouldBe(1)
      and("2.1") {
        count.incrementAndGet().shouldBe(2)
        scenario("2.1.1") {
          count.incrementAndGet().shouldBe(3)
        }
        scenario("2.1.2") {
          count.incrementAndGet().shouldBe(3)
        }
      }
    }
  }
}
