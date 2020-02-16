package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec

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
