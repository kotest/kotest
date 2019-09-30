package com.sksamuel.kotest.specs.feature

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.specs.FeatureSpec

class FeatureBangTest : FeatureSpec() {

  init {
    feature("!BangedFeature") {
      attemptToFail()
    }

    feature("NonBangedFeature") {
      and("!BangedAnd") {
        attemptToFail()
      }

      and("NonBangedAnd") {
        scenario("!BangedScenario") {
          attemptToFail()
        }
      }

      scenario("!BangedScenario") {
        attemptToFail()
      }
    }
  }
}