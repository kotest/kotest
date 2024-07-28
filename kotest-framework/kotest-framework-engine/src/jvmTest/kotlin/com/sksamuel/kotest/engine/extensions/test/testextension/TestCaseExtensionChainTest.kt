package com.sksamuel.kotest.engine.extensions.test.testextension

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.StringSpec

// tests that we can change extensions
class TestCaseExtensionChainTest : StringSpec() {

   object MyExt1 : TestCaseExtension {
      override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
         return if (testCase.descriptor.id.value == "test1")
            TestResult.Ignored()
         else
            execute(testCase)
      }
   }

   object MyExt2 : TestCaseExtension {
      override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
         return if (testCase.descriptor.id.value == "test2")
            TestResult.Ignored()
         else
            execute(testCase)
      }
   }

   override fun extensions() = listOf(MyExt1, MyExt2)

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
