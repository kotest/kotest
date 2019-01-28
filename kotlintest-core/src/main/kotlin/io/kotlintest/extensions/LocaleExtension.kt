package io.kotlintest.extensions

import io.kotlintest.TestCase
import io.kotlintest.TestResult
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

class TimezoneExtension(private val tz: TimeZone) : TestCaseExtension {
  override suspend fun intercept(testCase: TestCase,
                                 execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                 complete: suspend (TestResult) -> Unit) {
    val previous = TimeZone.getDefault()
    TimeZone.setDefault(tz)
    execute(testCase) { result ->
      TimeZone.setDefault(previous)
      complete(result)
    }
  }
}