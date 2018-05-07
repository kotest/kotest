package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.FreeSpec

class FreeSpecSharedInstanceDuplicateNameTest : FreeSpec() {

  override fun isInstancePerTest(): Boolean = false

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