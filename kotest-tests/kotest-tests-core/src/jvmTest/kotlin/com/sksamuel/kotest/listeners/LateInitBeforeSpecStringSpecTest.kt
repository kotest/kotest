package com.sksamuel.kotest.listeners

import io.kotest.core.spec.SpecConfiguration
import io.kotest.shouldBe
import io.kotest.core.spec.style.StringSpec

class LateInitBeforeSpecStringSpecTest : StringSpec() {

  private lateinit var string: String

  override fun beforeSpec(spec: SpecConfiguration) {
    string = "Hello"
  }

  init {
    "Hello should equal to Hello" {
      string shouldBe "Hello"
    }
  }
}
