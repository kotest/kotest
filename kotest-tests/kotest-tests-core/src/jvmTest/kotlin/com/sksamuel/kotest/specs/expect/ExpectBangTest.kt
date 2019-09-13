package com.sksamuel.kotest.specs.expect

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.specs.ExpectSpec

class ExpectBangTest : ExpectSpec() {

  init {
    context("!BangedContext") {
      attemptToFail()
    }

    context("NonBangedContext") {
      expect("!BangedExpected") {
        attemptToFail()
      }
    }

  }

}