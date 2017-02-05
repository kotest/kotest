package io.kotlintest.specs

class BehaviorSpecParenthesisTest : BehaviorSpec() {
  init {
    given("a sheet of string cells 4x4") {
      `when`("get existing cell by reference (like A1 or B2)") {
        then("should contain its value") {
          // noop
        }
      }
    }
  }
}