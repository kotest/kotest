package com.sksamuel.kotest.engine.spec.bangs

import io.kotest.core.spec.style.FreeSpec

class FreeSpecBangTest : FreeSpec() {

  init {
    "!BangedOuter" - {
      error("WHAP!")
    }

    "NonBangedOuter" - {
      "!BangedInner" {
        error("ZLONK!")
      }
    }
  }

}
