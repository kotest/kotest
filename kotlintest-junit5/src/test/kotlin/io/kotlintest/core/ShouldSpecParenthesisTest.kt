package io.kotlintest.core

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