package com.sksamuel.kotest.specs.funspec

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.specs.FunSpec

class FunSpecBangTest : FunSpec() {

  init {
    test("!BangedTest") {
      attemptToFail()
    }
  }

}