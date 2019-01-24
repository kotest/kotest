package com.sksamuel.kotlintest.specs.feature

import com.sksamuel.kotlintest.specs.attemptToFail
import io.kotlintest.specs.FeatureSpec

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