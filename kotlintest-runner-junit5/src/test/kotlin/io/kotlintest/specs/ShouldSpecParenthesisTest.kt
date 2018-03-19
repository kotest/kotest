package io.kotlintest.specs

import io.kotlintest.specs.ShouldSpec

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