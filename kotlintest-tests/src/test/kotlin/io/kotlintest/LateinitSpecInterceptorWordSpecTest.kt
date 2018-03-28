package io.kotlintest

import io.kotlintest.specs.WordSpec

class LateinitSpecInterceptorWordSpecTest : WordSpec() {

  private lateinit var string: String

  override fun interceptSpec(spec: Spec, process: () -> Unit) {
    string = "Hello"
    super.interceptSpec(spec, process)
  }

  init {
    "setting a late init var" should {
      "be supported by word spec" {
        string shouldBe "Hello"
      }
    }
  }
}