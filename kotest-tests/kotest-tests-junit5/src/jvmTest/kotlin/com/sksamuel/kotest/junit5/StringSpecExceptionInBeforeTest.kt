package com.sksamuel.kotest.junit5

import io.kotest.TestCase
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

class StringSpecExceptionInBeforeTest : StringSpec() {

  init {
    "a failing test" {
      1 shouldBe 2
    }

    "a passing test" {
      1 shouldBe 1
    }
  }

  override fun beforeTest(testCase: TestCase) {
    throw RuntimeException("oooff!!")
  }
}