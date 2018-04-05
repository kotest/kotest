package com.sksamuel.kotlintest.tests

import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class LateinitTestInterceptorStringSpecTest : StringSpec() {

  private lateinit var string: String

  override fun interceptTestCase(testCase: TestCase, test: () -> Unit) {
    string = "Hello"
    test()
  }

  init {
    "Hello should equal to Hello" {
      string shouldBe "Hello"
    }
  }
}