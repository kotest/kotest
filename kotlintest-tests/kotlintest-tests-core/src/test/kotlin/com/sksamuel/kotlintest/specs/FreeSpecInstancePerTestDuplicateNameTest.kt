package com.sksamuel.kotlintest.specs

import io.kotlintest.SpecIsolationMode
import io.kotlintest.fail
import io.kotlintest.specs.FreeSpec

class FreeSpecInstancePerTestDuplicateNameTest : FreeSpec() {

  override fun specIsolationMode() = SpecIsolationMode.InstancePerNode

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