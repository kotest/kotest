package com.sksamuel.kotest.engine.spec.bangs

import io.kotest.core.spec.style.StringSpec

class StringSpecBangTest : StringSpec() {

  init {
    "!BangedTest" {
      error("ZGRUPPP!")
    }
  }

}
