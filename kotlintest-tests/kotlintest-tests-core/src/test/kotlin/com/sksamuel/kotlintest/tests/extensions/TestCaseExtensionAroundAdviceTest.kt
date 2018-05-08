package com.sksamuel.kotlintest.tests.extensions

import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import io.kotlintest.specs.StringSpec

// this tests that we can manipulate the result of a test case from an extension
class TestCaseExtensionAroundAdviceTest : StringSpec() {

  class WibbleException : RuntimeException()

  object MyExt : TestCaseExtension {
    override fun intercept(context: TestCaseInterceptContext,
                           test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                           complete: (TestResult) -> Unit) {
      when {
        context.description.name == "test1" -> complete(TestResult.Ignored)
        context.description.name == "test2" -> test(context.config, {
          when (it.error) {
            is WibbleException -> complete(TestResult.Success)
            else -> complete(it)
          }
        })
        context.description.name == "test3" -> if (context.config.enabled) throw RuntimeException() else test(context.config, { complete(it) })
        context.description.name == "test4" -> test(context.config.copy(enabled = false), { complete(it) })
        else -> test(context.config, { complete(it) })
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