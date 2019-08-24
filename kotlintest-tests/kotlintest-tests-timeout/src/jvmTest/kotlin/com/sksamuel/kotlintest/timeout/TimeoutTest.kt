package com.sksamuel.kotlintest.timeout

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.delay
import java.lang.RuntimeException
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
class TimeoutTest : StringSpec() {

  init {

    "a blocked thread should timeout a test".config(timeout = 250.milliseconds) {
      Thread.sleep(1000)
    }

    "a suspended coroutine should timeout a test".config(timeout = 250.milliseconds) {
      delay(1000)
    }
  }

  override fun extensions(): List<SpecLevelExtension> = listOf(object : TestCaseExtension {
    override suspend fun intercept(testCase: TestCase,
                                   execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                   complete: suspend (TestResult) -> Unit) {
      execute(testCase) {
        when (it.status) {
          TestStatus.Failure, TestStatus.Error -> complete(TestResult.success(1000))
          else -> throw RuntimeException("${testCase.description} should fail")
        }
      }
    }
  })
}
