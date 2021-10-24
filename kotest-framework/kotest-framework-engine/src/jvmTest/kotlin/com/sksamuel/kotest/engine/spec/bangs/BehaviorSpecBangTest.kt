package com.sksamuel.kotest.engine.spec.bangs

import io.kotest.core.spec.style.BehaviorSpec

class BehaviorSpecBangTest : BehaviorSpec() {

  init {
    Given("!BangedGiven") {
      error("BAM!")
    }

    Given("NonBangedGiven") {
      And("!BangedGivenAnd") {
        error("BIFF!")
      }

      And("NonBangedGivenAnd") {
        When("!BangedWhen") {
          error("BOFF!")
        }

        When("NonBangedWhen") {
          And("!BangedWhenAnd") {
            error("CLANK!")
          }

          And("NonBangedWhenAnd") {
            Then("!BangedThen") {
              error("CRRAACK!")
            }
          }
        }
      }
    }
  }
}
