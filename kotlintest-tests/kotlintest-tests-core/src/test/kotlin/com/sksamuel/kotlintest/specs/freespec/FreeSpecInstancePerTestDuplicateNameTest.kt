package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.TestIsolationMode
import io.kotlintest.fail
import io.kotlintest.specs.FreeSpec

class FreeSpecInstancePerTestDuplicateNameTest : FreeSpec() {

  override fun isolationMode() = TestIsolationMode.InstancePerTest

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