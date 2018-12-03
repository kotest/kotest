package com.sksamuel.kotlintest.specs.stringspec

import io.kotlintest.TestCaseConfig
import io.kotlintest.TestIsolationMode
import io.kotlintest.specs.StringSpec

class StringSpecSingleInstanceDuplicateNameTest : StringSpec() {

  override fun isolationMode() = TestIsolationMode.SingleInstance

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