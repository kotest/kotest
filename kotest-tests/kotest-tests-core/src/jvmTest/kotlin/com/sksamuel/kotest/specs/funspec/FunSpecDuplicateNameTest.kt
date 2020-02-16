package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec

class FunSpecDuplicateNameTest : FunSpec() {
  init {
    test("wibble") { }
    try {
      test("wibble") {}
      throw RuntimeException("Must fail when adding duplicate root test name")
    } catch (e: IllegalArgumentException) {
    }
  }
}
