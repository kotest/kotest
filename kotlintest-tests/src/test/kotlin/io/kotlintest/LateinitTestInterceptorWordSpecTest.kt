package io.kotlintest

import io.kotlintest.runner.junit5.specs.WordSpec

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