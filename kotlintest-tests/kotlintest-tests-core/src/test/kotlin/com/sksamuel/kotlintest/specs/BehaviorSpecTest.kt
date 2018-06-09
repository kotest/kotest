package com.sksamuel.kotlintest.specs

import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

class BehaviorSpecTest : BehaviorSpec() {
  init {
    given("a") {
      `when`("b") {
        then("c") {
          1.shouldBeLessThan(2)
        }
      }
    }
  }
}

fun boom() = 1 / 0

class TestBoom: BehaviorSpec() {
  init {
    Given("Test") {
      boom()
      When("running here a code that potentially throws an exception") {
        Then("no info about that exception") {
          1 shouldBe 1
        }
      }
    }
  }
}