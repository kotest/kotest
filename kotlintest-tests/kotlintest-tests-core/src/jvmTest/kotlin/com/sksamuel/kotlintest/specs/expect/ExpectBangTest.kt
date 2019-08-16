package com.sksamuel.kotlintest.specs.expect

import com.sksamuel.kotlintest.specs.attemptToFail
import io.kotlintest.specs.ExpectSpec

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