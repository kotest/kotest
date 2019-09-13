package com.sksamuel.kotest.extensions

import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestCaseExtension
import io.kotest.specs.StringSpec
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

// this tests that we can manipulate the result of a test case from an extension
@ExperimentalTime
class TestCaseExtensionAroundAdviceTest : StringSpec() {

   class WibbleException : RuntimeException()

   object MyExt : TestCaseExtension {
      override suspend fun intercept(testCase: TestCase,
                                     execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                     complete: suspend (TestResult) -> Unit) {
         when {
            testCase.description.name == "test1" -> complete(TestResult.Ignored)
            testCase.description.name == "test2" -> execute(testCase) {
               when (it.error) {
                  is WibbleException -> complete(TestResult.success(Duration.ZERO))
                  else -> complete(it)
               }
            }
            testCase.description.name == "test3" ->
               if (testCase.config.enabled) throw RuntimeException() else execute(testCase) { complete(it) }
            testCase.description.name == "test4" ->
               execute(testCase.copy(config = testCase.config.copy(enabled = false))) { complete(it) }
            else -> execute(testCase) { complete(it) }
         }
      }
   }

   override fun extensions(): List<SpecLevelExtension> = listOf(MyExt)

   init {
      // this exception should not be thrown as the extension will skip evaluation of the test
      "test1" {
         throw RuntimeException()
      }
      // this exception will be thrown but then the test extension will override the failed result to return a success
      "test2" {
         throw WibbleException()
      }
      // the config for this test should be carried through to the extension
      "test3".config(enabled = false) {
      }
      //  config for this test should be overriden so that the test is actually disabled, and therefore the exception will not be thrown
      "test4".config(enabled = true) {
         throw RuntimeException()
      }
   }
}
