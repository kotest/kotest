package com.sksamuel.kotlintest.tests

import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class LateinitTestInterceptorWordSpecTest : WordSpec() {

  private lateinit var string: String

  override fun interceptTestCase(testCase: TestCase, test: () -> Unit) {
    string = "Hello"
    test()
  }

  init {
    "setting a late init var" should {
      "be supported by word spec" {
        string shouldBe "Hello"
      }
    }
  }
}