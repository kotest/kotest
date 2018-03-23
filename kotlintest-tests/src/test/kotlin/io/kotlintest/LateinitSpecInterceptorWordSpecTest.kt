package io.kotlintest

import io.kotlintest.runner.junit5.specs.WordSpec

class LateinitSpecInterceptorWordSpecTest : WordSpec() {

  private lateinit var string: String

  override fun interceptSpec(process: () -> Unit) {
    string = "Hello"
    super.interceptSpec(process)
  }

  init {
    "setting a late init var" should {
      "be supported by word spec" {
        string shouldBe "Hello"
      }
    }
  }
}