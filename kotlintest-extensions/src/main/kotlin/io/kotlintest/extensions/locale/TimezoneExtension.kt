package io.kotlintest.extensions.locale

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestCaseExtension
import java.util.*

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