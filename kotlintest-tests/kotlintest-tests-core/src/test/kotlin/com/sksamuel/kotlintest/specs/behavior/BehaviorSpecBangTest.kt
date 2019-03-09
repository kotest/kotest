package com.sksamuel.kotlintest.specs.behavior

import com.sksamuel.kotlintest.specs.attemptToFail
import io.kotlintest.specs.BehaviorSpec

class BehaviorSpecBangTest : BehaviorSpec() {

  init {
    Given("!BangedGiven") {
      attemptToFail()
    }

    Given("NonBangedGiven") {
      And("!BangedGivenAnd") {
        attemptToFail()
      }

      And("NonBangedGivenAnd") {
        When("!BangedWhen") {
          attemptToFail()
        }

        When("NonBangedWhen") {
          And("!BangedWhenAnd") {
            attemptToFail()
          }

          And("NonBangedWhenAnd") {
            Then("!BangedThen") {
              attemptToFail()
            }
          }
        }
      }
    }
  }
}