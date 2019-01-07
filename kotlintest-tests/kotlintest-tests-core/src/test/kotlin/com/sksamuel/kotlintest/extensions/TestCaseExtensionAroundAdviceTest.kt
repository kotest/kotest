package com.sksamuel.kotlintest.extensions

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.specs.StringSpec

// this tests that we can manipulate the result of a test case from an extension
class TestCaseExtensionAroundAdviceTest : StringSpec() {

  class WibbleException : RuntimeException()

  object MyExt : TestCaseExtension {
    override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit, complete: suspend (TestResult) -> Unit) {
      when {
        testCase.description.name == "test1" -> complete(TestResult.Ignored)
        testCase.description.name == "test2" -> execute(testCase) {
          when (it.error) {
            is WibbleException -> complete(TestResult.Success)
            else -> complete(it)
          }
        }
        testCase.description.name == "test3" -> if (testCase.config.enabled) throw RuntimeException() else execute(testCase) { complete(it) }
        testCase.description.name == "test4" -> execute(testCase.copy(config = testCase.config.copy(enabled = false))) { complete(it) }
        else -> execute(testCase) { complete(it) }
      }
    }
  }

  override fun extensions(): List<SpecLevelExtension> = listOf(MyExt)

  init {
    "test1" {
      // this exception should not be thrown as the extension will skip evaluation of the test
      throw RuntimeException()
    }
    "test2" {
      // this exception will be thrown but then the test extension will override and return success
      throw WibbleException()
    }
    "test3".config(enabled = false) {
      // the config for this test should be carried through to the interceptor
    }
    "test4".config(enabled = true) {
      //  config for this test should be overriden so that the value set on the test case itself is overruled
      throw RuntimeException()
    }
  }
}