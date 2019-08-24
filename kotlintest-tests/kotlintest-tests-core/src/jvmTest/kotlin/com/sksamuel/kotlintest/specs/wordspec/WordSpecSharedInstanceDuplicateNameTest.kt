package com.sksamuel.kotlintest.specs.wordspec

import io.kotlintest.core.TestCaseConfig
import io.kotlintest.specs.WordSpec

class WordSpecSharedInstanceDuplicateNameTest : WordSpec() {

  override val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig(invocations = 2)

  init {
    "context" should {
      "foo" {}
      try {
        "foo" {}
        throw RuntimeException("Must fail when adding duplicate root test name")
      } catch (e: IllegalStateException) {
      }
      "not count multiple invocations as the same test".config(invocations = 3) {}
    }
  }
}
