package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.IsolationMode
import io.kotlintest.fail
import io.kotlintest.specs.FreeSpec

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