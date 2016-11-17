package io.kotlintest.specs

class BehaviorSpecParenthesisTest : BehaviorSpec() {
  init {
    Given("a sheet of string cells 4x4") {
      When("get existing cell by reference (like A1 or B2)") {
        Then("should contain its value") {
          // noop
        }
      }
    }
  }
}