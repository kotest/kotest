package com.sksamuel.kotlintest.specs.stringspec

import com.sksamuel.kotlintest.specs.attemptToFail
import io.kotlintest.specs.StringSpec

class StringSpecBangTest : StringSpec() {

  init {
    "!BangedTest" {
      attemptToFail()
    }
  }

}