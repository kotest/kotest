package com.sksamuel.kotest.specs.behavior

import io.kotest.specs.BehaviorSpec

class BehaviorSpecExample : BehaviorSpec() {

  init {
    given("a sheet of string cells 4x4") {
      `when`("get existing cell by reference (like A1 or B2)") {
        then("should contain its value") {
          // test here
        }
        then("should set the datatype for the value") {
          // test here
        }
      }
      `when`("adding a new cell") {
        then("the sheet should enlarge") {
          // test here
        }
      }
    }
  }
}
