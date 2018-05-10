package com.sksamuel.kotlintest.specs

import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.specs.BehaviorSpec

class BehaviorSpecTest : BehaviorSpec() {
  init {
    given("a") {
      `when`("b") {
        then("c") {
          1.shouldBeLessThan(2)
        }
      }
    }
  }
}