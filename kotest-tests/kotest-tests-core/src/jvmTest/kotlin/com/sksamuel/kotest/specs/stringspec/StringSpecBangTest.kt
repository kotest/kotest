package com.sksamuel.kotest.specs.stringspec

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.core.spec.style.StringSpec

class StringSpecBangTest : StringSpec() {

  init {
    "!BangedTest" {
      attemptToFail()
    }
  }

}
