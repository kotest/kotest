package com.sksamuel.samples.gradle

import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.kotlintest.shouldThrowAny

class BehaviorSpecExampleTest : BehaviorSpec() {

  init {
    given("a non empty stack") {
      `when`("invoking pop") {
        then("the top most element should be removed and returned") {
            1 shouldBe 2
        }
      }
      `when`("invoking push") {
        then(name = "a new element should be added to the top of the stack") {
          "qweqwe".shouldNotBeBlank()
        }
      }
    }
  }
}