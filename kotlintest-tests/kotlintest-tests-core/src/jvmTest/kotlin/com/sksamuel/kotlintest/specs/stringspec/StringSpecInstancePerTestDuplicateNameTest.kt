package com.sksamuel.kotlintest.specs.stringspec

import io.kotlintest.IsolationMode
import io.kotlintest.specs.StringSpec

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
