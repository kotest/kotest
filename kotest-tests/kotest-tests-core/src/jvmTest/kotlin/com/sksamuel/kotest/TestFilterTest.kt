package com.sksamuel.kotest

import io.kotest.core.filters.TestFilter
import io.kotest.core.filters.TestFilterResult
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.Description

class TestFilterTest : StringSpec() {

  var a = false
  var b = false

  init {
    "aa should run" {
      a = true
    }
    // this test will be ignored the test case filter that we have registered in project config
    "bb should be ignored" {
      1 shouldBe 2
    }
  }

  override fun afterTest(testCase: TestCase, result: TestResult) {
    when (testCase.description.name.displayName()) {
      "aa should run" -> result.status shouldBe TestStatus.Success
      "bb should be ignored" -> result.status shouldBe TestStatus.Ignored
    }
    a shouldBe true
  }
}

object TestFilterTestFilter : TestFilter {
  override fun filter(description: Description): TestFilterResult {
    return when (description.name.displayName()) {
      "bb should be ignored" -> TestFilterResult.Exclude
      else -> TestFilterResult.Include
    }
  }
}
