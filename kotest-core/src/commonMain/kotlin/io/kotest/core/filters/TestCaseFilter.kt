package io.kotest.core.filters

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * A [TestCaseFilter] can be used to filter tests before they are executed.
 * They are passed to the Kotest Engine at runtime.
 *
 * In this way it is similar to a [TestCaseExtension] but with a specialized purpose
 * and therefore simpler to use.
 */
interface TestCaseFilter : Filter {

   /**
    * This method is invoked with the test [Description] and the result
    * used to determine if the test should be included or not.
    */
   fun filter(description: Description): TestFilterResult
}

/**
 * Creates an extension from this test case filter so it can be used as an extension.
 */
fun TestCaseFilter.toExtension(): TestCaseExtension = object : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      return when (filter(testCase.description)) {
         TestFilterResult.Include -> execute(testCase)
         TestFilterResult.Exclude -> TestResult.Ignored
      }
   }
}

enum class TestFilterResult {
   Include, Exclude
}

