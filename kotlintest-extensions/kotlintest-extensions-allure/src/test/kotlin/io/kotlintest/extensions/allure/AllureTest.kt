package io.kotlintest.extensions.allure

import io.kotlintest.specs.WordSpec

class AllureTest : WordSpec() {

  override fun listeners() = listOf(AllureExtension)

  init {
    "this is a test scope" should {
      "this is a test case" {

      }
    }
  }
}