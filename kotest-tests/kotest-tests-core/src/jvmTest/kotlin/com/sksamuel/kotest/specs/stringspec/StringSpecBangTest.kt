package com.sksamuel.kotest.specs.stringspec

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.specs.StringSpec

class StringSpecBangTest : StringSpec() {

  init {
    "!BangedTest" {
      attemptToFail()
    }
  }

}