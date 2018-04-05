package com.sksamuel.kotlintest.tests

import io.kotlintest.Spec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class LateinitSpecInterceptorStringSpecTest : StringSpec() {

  private lateinit var string: String

  override fun interceptSpec(spec: Spec, process: () -> Unit) {
    string = "Hello"
    super.interceptSpec(spec, process)
  }

  init {
    "Hello should equal to Hello" {
      string shouldBe "Hello"
    }
  }
}