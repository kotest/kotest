package com.sksamuel.kotest.listeners

import io.kotest.core.test.Description
import io.kotest.core.spec.SpecConfiguration
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

class LateInitBeforeSpecStringSpecTest : StringSpec() {

  private lateinit var string: String

  override fun beforeSpec(description: Description, spec: SpecConfiguration) {
    string = "Hello"
  }

  init {
    "Hello should equal to Hello" {
      string shouldBe "Hello"
    }
  }
}
