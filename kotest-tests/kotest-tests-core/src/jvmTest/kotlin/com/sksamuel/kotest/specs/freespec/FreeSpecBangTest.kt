package com.sksamuel.kotest.specs.freespec

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.core.spec.style.FreeSpec

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
