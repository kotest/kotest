package com.sksamuel.kotest.specs.behavior

import io.kotest.core.spec.style.BehaviorSpec

class BehaviorSpecParenthesisTest : BehaviorSpec() {
  init {
    given("a sheet of string cells 4x4") {
      `when`("get existing cell by reference (like A1 or B2)") {
        then("should contain its value") {
          // noop
        }
        then("should set the datatype for the value") {
          // noop
        }
      }
      `when`("adding a new cell") {
        then("the sheet should enlarge") {
          // noop
        }
      }
    }
  }
}
