package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.TestCaseConfig
import io.kotest.specs.WordSpec

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
