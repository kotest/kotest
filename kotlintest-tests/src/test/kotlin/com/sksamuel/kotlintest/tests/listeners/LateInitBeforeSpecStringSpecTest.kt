package com.sksamuel.kotlintest.tests.listeners

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class LateInitBeforeSpecStringSpecTest : StringSpec() {

  private lateinit var string: String

  override fun beforeSpec(description: Description, spec: Spec) {
    string = "Hello"
  }

  init {
    "Hello should equal to Hello" {
      string shouldBe "Hello"
    }
  }
}