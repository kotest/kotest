package io.kotlintest

import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext

interface TestCaseFilter : ProjectLevelFilter {
  fun filter(description: Description): TestFilterResult
  fun toExtension() = object : TestCaseExtension {
    override suspend fun intercept(context: TestCaseInterceptContext, test: suspend (TestCaseConfig, suspend (TestResult) -> Unit) -> Unit, complete: suspend (TestResult) -> Unit) {
      when (filter(context.description)) {
        TestFilterResult.Include -> test(context.config, complete)
        TestFilterResult.Ignore -> complete(TestResult.Ignored)
      }
    }
  }
}

interface ProjectLevelFilter

enum class TestFilterResult {
  Include, Ignore
}

