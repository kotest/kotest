package io.kotlintest

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

class OneInstancePerTestTest : ShouldSpec() {

  override val oneInstancePerTest = true

  init {
    var count = 0
    should("be 0") {
      count shouldBe 0
      count = 100
    }
    should("be 0") {
      count shouldBe 0
      count = 100
    }
    should("be 0") {
      count shouldBe 0
      count = 100
    }
    should("still be 0") {
      count shouldBe 0
    }
  }
}