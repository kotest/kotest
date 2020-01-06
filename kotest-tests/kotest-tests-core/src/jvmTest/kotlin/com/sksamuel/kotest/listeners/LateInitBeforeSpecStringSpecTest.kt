package com.sksamuel.kotest.listeners

import io.kotest.core.Description
import io.kotest.SpecClass
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

class LateInitBeforeSpecStringSpecTest : StringSpec() {

  private lateinit var string: String

  override fun beforeSpec(description: Description, spec: SpecClass) {
    string = "Hello"
  }

  init {
    "Hello should equal to Hello" {
      string shouldBe "Hello"
    }
  }
}
