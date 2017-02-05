package io.kotlintest.specs

class BehaviorSpecParenthesisTest : BehaviorSpec() {
  init {
    given("a sheet of string cells 4x4") {
      println("given")
      `when`("get existing cell by reference (like A1 or B2)") {
        println("when")
        then("should contain its value") {
          println("then")
          // noop
        }
      }
    }
  }
}