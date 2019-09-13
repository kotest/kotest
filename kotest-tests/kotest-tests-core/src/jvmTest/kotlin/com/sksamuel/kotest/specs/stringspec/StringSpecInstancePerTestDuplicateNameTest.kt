package com.sksamuel.kotest.specs.stringspec

import io.kotest.IsolationMode
import io.kotest.specs.StringSpec

class StringSpecInstancePerTestDuplicateNameTest : StringSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  init {
    "foo" {}
    try {
      "foo" {}
      throw RuntimeException("Must fail when adding duplicate root test name")
    } catch (e: IllegalArgumentException) {
    }
  }
}
