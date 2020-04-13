package com.sksamuel.kotest.specs.funspec

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.core.spec.style.FunSpec

class FunSpecBangTest : FunSpec() {

  init {
    test("!BangedTest") {
      attemptToFail()
    }
  }

}
