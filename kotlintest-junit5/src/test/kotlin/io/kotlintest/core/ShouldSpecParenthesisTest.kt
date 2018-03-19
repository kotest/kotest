package io.kotlintest.core

import io.kotlintest.core.specs.ShouldSpec

class ShouldSpecParenthesisTest : ShouldSpec() {
  init {
    "nested (parenths)" {
      should("parenthesis work (here)") {
      }
    }
    should("parenthesis work (here)") {
    }
  }
}