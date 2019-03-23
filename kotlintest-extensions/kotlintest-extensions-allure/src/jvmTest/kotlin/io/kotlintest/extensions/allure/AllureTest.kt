package io.kotlintest.extensions.allure

import io.kotlintest.matchers.haveLength
import io.kotlintest.specs.WordSpec

class AllureTest : WordSpec() {

  override fun listeners() = listOf(AllureExtension)

  init {

    "this is a test scope" should {
      "this is a test case" {
        "sammy" should haveLength(5)
      }
    }
  }
}