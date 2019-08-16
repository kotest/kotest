package com.sksamuel.kotlintest.inspectors

import io.kotlintest.inspectors.buildAssertionError
import io.kotlintest.specs.WordSpec

class FuncsTest : WordSpec() {

  init {
    //TODO: exception catch
    "buildAssertionError" should {
      "do something" {
        buildAssertionError<String>("", emptyList())
      }
    }
  }
}
