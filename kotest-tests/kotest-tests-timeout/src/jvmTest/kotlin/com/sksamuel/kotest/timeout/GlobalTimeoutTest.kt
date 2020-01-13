package com.sksamuel.kotest.timeout

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestCaseExtension
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
class GlobalTimeoutTest : StringSpec() {

  init {

    "a blocked thread should timeout if global timeout is applied" {
      Thread.sleep(2500)
    }

    "a suspended coroutine should timeout if a global timeout is applied" {
      delay(2500)
    }

  }

  override fun extensions(): List<SpecLevelExtension> = listOf(object : TestCaseExtension {
    override suspend fun intercept(testCase: TestCase,
                                   execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                   complete: suspend (TestResult) -> Unit) {
      execute(testCase) {
        when (it.status) {
          TestStatus.Failure, TestStatus.Error -> complete(
              TestResult.success(1000.milliseconds))
          else -> throw RuntimeException("This should not occur")
        }
      }
    }
  })

}
