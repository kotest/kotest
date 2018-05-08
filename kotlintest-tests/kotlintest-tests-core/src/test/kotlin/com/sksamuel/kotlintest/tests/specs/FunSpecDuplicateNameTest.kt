package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.FunSpec

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