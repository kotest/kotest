package com.sksamuel.kotlintest.tests.listeners

import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

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