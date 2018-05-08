package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.StringSpec

class StringSpecInstancePerTestDuplicateNameTest : StringSpec() {

  override fun isInstancePerTest(): Boolean = true

  init {
    "foo" {}
    try {
      "foo" {}
      throw RuntimeException("Must fail when adding duplicate root test name")
    } catch (e: IllegalArgumentException) {
    }
  }
}