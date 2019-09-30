package com.sksamuel.kotest.extensions

import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestCaseExtension
import io.kotest.specs.StringSpec

class TestCaseExtensionChainTest : StringSpec() {

  class WibbleException : RuntimeException()

  object MyExt1 : TestCaseExtension {
    override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit, complete: suspend (TestResult) -> Unit) {
      if (testCase.description.name == "test1")
        complete(TestResult.Ignored)
      else
        execute(testCase) { complete(it) }
    }
  }

  object MyExt2 : TestCaseExtension {
    override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit, complete: suspend (TestResult) -> Unit) {
      if (testCase.description.name == "test2")
        complete(TestResult.Ignored)
      else
        execute(testCase) { complete(it) }
    }
  }

  override fun extensions(): List<SpecLevelExtension> = listOf(MyExt1, MyExt2)

  init {
    "test1" {
      // this exception should not be thrown as the first interceptor should ignore it
      throw RuntimeException()
    }
    "test2" {
      // this exception should not be thrown as the second interceptor should ignore it
      throw RuntimeException()
    }
    "test3" {

    }
  }
}