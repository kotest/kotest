@file:Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")

package com.sksamuel.kotest.framework.engine

import io.kotest.assertions.AssertionErrorBuilder.Companion.fail
import io.kotest.core.annotation.Description
import io.kotest.core.annotation.Issue
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult

class MarkTestAsIgnored : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      return if (testCase.name.name == "should not run") TestResult.Ignored(null) else execute(testCase)
   }
}

@Issue("https://github.com/kotest/kotest/issues/3318")
@Description("tests that an ignored test on JS shows up as ignored and not failed")
class TestIgnoredExtensionTest : FunSpec() {

   init {
      extension(MarkTestAsIgnored())

      test("should not run").config(enabledOrReasonIf = { Enabled.enabled }) {
         fail("This should not run as the test interceptor should skip it")
      }

      test("!ignored1") {}
      xtest("ignored2") {}
   }

}
