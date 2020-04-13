package com.sksamuel.kotest.specs.behavior

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.core.spec.style.BehaviorSpec

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
