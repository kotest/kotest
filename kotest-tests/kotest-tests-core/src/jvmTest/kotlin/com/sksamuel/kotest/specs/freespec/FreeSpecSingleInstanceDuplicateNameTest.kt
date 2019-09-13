package com.sksamuel.kotest.specs.freespec

import io.kotest.specs.FreeSpec

class FreeSpecSingleInstanceDuplicateNameTest : FreeSpec() {

  init {

    "wibble" { }
    try {
      "wibble" {}
      throw RuntimeException("Must fail when adding duplicate root test name")
    } catch (e: IllegalArgumentException) {
    }

    "wobble" - {
      "foo" { }
      try {
        "foo" { }
        throw RuntimeException("Must fail when adding duplicate nested test name")
      } catch (e: IllegalStateException) {
      }

    }

  }
}
