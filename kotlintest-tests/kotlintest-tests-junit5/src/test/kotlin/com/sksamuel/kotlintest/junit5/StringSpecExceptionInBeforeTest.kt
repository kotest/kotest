package com.sksamuel.kotlintest.junit5

import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

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