package io.kotlintest

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

class SharedInstanceForTestsTest : ShouldSpec() {

  override val oneInstancePerTest = false

  init {
    var count = 0
    should("be 0") {
      count shouldBe 0
      count = 100
    }
    should("be 0") {
      count shouldBe 100
      count = 200
    }
    should("be 100") {
      count shouldBe 200
    }
  }
}