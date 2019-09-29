package com.sksamuel.kotest.specs.stringspec

import io.kotest.IsolationMode
import io.kotest.core.TestCaseConfig
import io.kotest.specs.StringSpec

class StringSpecSingleInstanceDuplicateNameTest : StringSpec() {

  override fun isolationMode() = IsolationMode.SingleInstance

  override val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig(invocations = 2)

  init {
    "foo" {}
    try {
      "foo" {}
      throw RuntimeException("Must fail when adding duplicate root test name")
    } catch (e: IllegalArgumentException) {
    }
    "should not count multiple invocations as the same test".config(invocations = 3) {}
  }
}
