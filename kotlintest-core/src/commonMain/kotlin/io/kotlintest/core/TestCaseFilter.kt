package io.kotlintest.core

import io.kotlintest.Description
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.ProjectLevelFilter
import io.kotlintest.extensions.TestCaseExtension

interface TestCaseFilter : ProjectLevelFilter {
  fun filter(description: Description): TestFilterResult
  fun toExtension() = object : TestCaseExtension {
    override suspend fun intercept(testCase: TestCase,
                                   execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                   complete: suspend (TestResult) -> Unit) {
      when (filter(testCase.description)) {
        TestFilterResult.Include -> execute(testCase, complete)
        TestFilterResult.Exclude -> complete(TestResult.Ignored)
      }
    }
  }
}

object IncludeAllTestCaseFilter : TestCaseFilter {
  override fun filter(description: Description): TestFilterResult = TestFilterResult.Include
}

enum class TestFilterResult {
  Include, Exclude
}

