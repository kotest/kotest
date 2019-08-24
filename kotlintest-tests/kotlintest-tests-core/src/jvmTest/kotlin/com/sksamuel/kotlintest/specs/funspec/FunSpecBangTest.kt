package com.sksamuel.kotlintest.specs.funspec

import com.sksamuel.kotlintest.specs.attemptToFail
import io.kotlintest.specs.FunSpec

class FunSpecBangTest : FunSpec() {

  init {
    test("!BangedTest") {
      attemptToFail()
    }
  }

}