package com.sksamuel.kotlintest.junit5

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class StringSpecTestCase : StringSpec({

  "a failing test" {
    1 shouldBe 2
  }

  "a passing test" {
    1 shouldBe 1
  }

  "an erroring test" {
    throw RuntimeException()
  }

  "a skipped test".config(enabled = false) {
  }

})