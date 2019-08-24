package com.sksamuel.kotlintest

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

class SharedInstanceForTestsTest : ShouldSpec() {

  init {
    var count = 0
    should("be 0") {
      count shouldBe 0
      count = 100
    }
    should("be 100") {
      count shouldBe 100
      count = 200
    }
    should("be 200") {
      count shouldBe 200
    }
  }
}