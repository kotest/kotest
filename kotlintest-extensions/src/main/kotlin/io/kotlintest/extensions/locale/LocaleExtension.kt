package io.kotlintest.extensions.locale

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestCaseExtension
import java.util.*

class LocaleExtension(private val locale: Locale) : TestCaseExtension {
  override suspend fun intercept(testCase: TestCase,
                                 execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                 complete: suspend (TestResult) -> Unit) {
    val previous = Locale.getDefault()
    Locale.setDefault(locale)
    execute(testCase) { result ->
      Locale.setDefault(previous)
      complete(result)
    }
  }
}