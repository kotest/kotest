package com.sksamuel.kotest.runner.junit5

import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec

internal class StringSpecTestCase : StringSpec({

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
