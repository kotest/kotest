package com.sksamuel.kotest.specs.shouldspec

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.core.spec.style.ShouldSpec

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
