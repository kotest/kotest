package com.sksamuel.kotest.junit5

import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

class StringSpecExceptionInAfterTest : StringSpec() {

  init {
    "a failing test" {
      1 shouldBe 2
    }

    "a passing test" {
      1 shouldBe 1
    }
  }

  override fun afterTest(testCase: TestCase, result: TestResult) {
    throw RuntimeException("craack!!")
  }
}
