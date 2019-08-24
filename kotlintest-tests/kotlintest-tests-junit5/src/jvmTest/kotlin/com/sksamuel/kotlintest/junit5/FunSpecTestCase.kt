package com.sksamuel.kotlintest.junit5

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class FunSpecTestCase : FunSpec({

  test("a failing test") {
    1 shouldBe 2
  }

  test("a passing test") {
    1 shouldBe 1
  }

  test("an erroring test") {
    throw RuntimeException()
  }

  test("a skipped test").config(enabled = false) {
  }

})