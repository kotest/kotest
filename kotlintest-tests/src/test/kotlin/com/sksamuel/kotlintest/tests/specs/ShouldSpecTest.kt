package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.specs.ShouldSpec

class ShouldSpecTest : ShouldSpec() {

  init {
    "a context" {
      should("a test") {
        1.shouldBeLessThan(2)
      }
      should("a test with config").config(invocations = 3) {
        1.shouldBeLessThan(2)
      }
      "a nested context" {
        should("a test") {
          1.shouldBeLessThan(2)
        }
      }
      should("a test without a parent context") {
        1.shouldBeLessThan(2)
      }
    }
  }
}