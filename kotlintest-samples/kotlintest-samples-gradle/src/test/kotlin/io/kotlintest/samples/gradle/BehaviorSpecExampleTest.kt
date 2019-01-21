package io.kotlintest.samples.gradle

import io.kotlintest.specs.BehaviorSpec

class BehaviorSpecExampleTest : BehaviorSpec() {

  init {
    given("a non empty stack") {
      `when`("invoking pop") {
        then("the top most element should be removed and returned") {
          // test here
        }
      }
      `when`("invoking push") {
        then("a new element should be added to the top of the stack") {
          // test here
        }
      }
    }
  }
}