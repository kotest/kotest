package com.sksamuel.kotlintest

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class OneInstancePerTestTest : FunSpec() {

  override fun isInstancePerTest(): Boolean = true

  init {
    var count = 0
    test("be 0") {
      count shouldBe 0
      count = 100
    }
    test("be 0 part 2") {
      count shouldBe 0
      count = 100
    }
    test("be 0 part 3") {
      count shouldBe 0
      count = 100
    }
    test("still be 0") {
      count shouldBe 0
    }
  }
}