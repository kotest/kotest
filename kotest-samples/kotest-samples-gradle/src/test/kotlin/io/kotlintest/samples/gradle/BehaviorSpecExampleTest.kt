package com.sksamuel.samples.gradle

import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.shouldBe
import io.kotest.specs.BehaviorSpec
import io.kotest.shouldThrowAny

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