package com.sksamuel.kotlintest.timeout

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.delay
import java.lang.RuntimeException
import java.time.Duration

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
          TestStatus.Failure, TestStatus.Error -> complete(TestResult.success(Duration.ofSeconds(1)))
          else -> throw RuntimeException("This should not occur")
        }
      }
    }
  })

}
