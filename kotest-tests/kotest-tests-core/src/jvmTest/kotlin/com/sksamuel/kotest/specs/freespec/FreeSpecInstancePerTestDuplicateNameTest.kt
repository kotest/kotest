package com.sksamuel.kotest.specs.freespec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec

class FreeSpecInstancePerTestDuplicateNameTest : FreeSpec() {

  override fun isolationMode() = IsolationMode.InstancePerTest

  init {

    "wibble" { }
    try {
      "wibble" {
        fail("error should be thrown")
      }
    } catch (e: IllegalArgumentException) {
    }

    "wobble" - {
      "foo" { }
      try {
        "foo" {
          fail("error should be thrown")
        }
      } catch (e: IllegalStateException) {
      }
    }
  }
}
