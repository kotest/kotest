package com.sksamuel.kotlintest.specs.behavior

import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import java.util.concurrent.atomic.AtomicInteger

class BehaviorSpecTest : BehaviorSpec() {
  init {
    given("a") {
      `when`("b") {
        then("c") {
          1.shouldBeLessThan(2)
        }
        val counter = AtomicInteger(0)
        then("with config").config(invocations = 3) {
          counter.incrementAndGet()
        }
        counter.get() shouldBe 3
      }
    }
  }
}