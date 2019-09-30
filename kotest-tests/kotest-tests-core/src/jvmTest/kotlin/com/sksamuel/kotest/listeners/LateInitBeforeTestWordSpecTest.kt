package com.sksamuel.kotest.listeners

import io.kotest.Description
import io.kotest.shouldBe
import io.kotest.specs.WordSpec

class LateInitBeforeTestWordSpecTest : WordSpec() {

  private lateinit var string: String

  override fun beforeTest(description: Description) {
    string = "Hello"
  }

  init {
    "setting a late init var" should {
      "be supported by word spec" {
        string shouldBe "Hello"
      }
    }
  }
}