package io.kotlintest

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class InterceptTestCaseTest : StringSpec() {

  override val oneInstancePerTest = true

  var count = 0

  override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
    count = 100
    test()
  }

  init {
    "field should have value assigned in interceptTestCase" {
      count shouldBe 100
    }
  }
}