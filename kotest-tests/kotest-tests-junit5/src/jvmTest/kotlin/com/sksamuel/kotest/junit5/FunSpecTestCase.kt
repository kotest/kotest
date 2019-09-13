package com.sksamuel.kotest.junit5

import io.kotest.shouldBe
import io.kotest.specs.FunSpec

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