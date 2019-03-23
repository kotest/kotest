package com.sksamuel.kotlintest.specs.shouldspec

import com.sksamuel.kotlintest.specs.attemptToFail
import io.kotlintest.specs.ShouldSpec

class ShouldSpecBangTest : ShouldSpec() {

  init {
    should("!BangedShould") {
      attemptToFail()
    }

    "!BangedContext" {
      attemptToFail()
    }

    "NonBangedOuter" {
      "!BangedInner" {
        attemptToFail()
      }
      "NonBangedInner" {
        should("!BangedShould") {
          attemptToFail()
        }
      }
    }
  }
}