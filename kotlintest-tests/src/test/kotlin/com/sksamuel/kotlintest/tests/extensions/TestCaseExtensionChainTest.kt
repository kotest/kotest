package com.sksamuel.kotlintest.tests.extensions

import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import io.kotlintest.specs.StringSpec

class TestCaseExtensionChainTest : StringSpec() {

  class WibbleException : RuntimeException()

  object MyExt1 : TestCaseExtension {
    override fun intercept(context: TestCaseInterceptContext,
                           test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                           complete: (TestResult) -> Unit) {
      if (context.description.name == "test1")
        complete(TestResult.Ignored)
      else
        test(context.config, { complete(it) })
    }
  }

  object MyExt2 : TestCaseExtension {
    override fun intercept(context: TestCaseInterceptContext,
                           test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                           complete: (TestResult) -> Unit) {
      if (context.description.name == "test2")
        complete(TestResult.Ignored)
      else
        test(context.config, { complete(it) })
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