package com.sksamuel.kotest.junit5

import io.kotest.shouldBe
import io.kotest.specs.StringSpec

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