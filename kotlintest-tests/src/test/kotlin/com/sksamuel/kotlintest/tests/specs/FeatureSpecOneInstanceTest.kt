package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec
import java.util.concurrent.atomic.AtomicInteger

class FeatureSpecOneInstanceTest : FeatureSpec() {

  override fun isInstancePerTest(): Boolean = true

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