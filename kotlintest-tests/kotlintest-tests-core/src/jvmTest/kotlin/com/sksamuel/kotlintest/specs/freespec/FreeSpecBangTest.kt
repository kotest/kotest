package com.sksamuel.kotlintest.specs.freespec

import com.sksamuel.kotlintest.specs.attemptToFail
import io.kotlintest.specs.FreeSpec

class FreeSpecBangTest : FreeSpec() {

  init {
    "!BangedOuter" - {
      attemptToFail()
    }

    "NonBangedOuter" - {
      "!BangedInner" {
        attemptToFail()
      }
    }
  }

}