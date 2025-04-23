package io.kotest.engine.spec

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.isRootTest

/**
 * A mutable store of results, which can be queried to check if a test has already been run,
 * or if a entire subtree has completed, etc.
 *
 * This class is thread safe, so can be used from multiple test executors concurrently.
 */
internal class TestResults {

   // the presence of a test case in this map indicates that the test has started
   // once the test completes, the result will no longer be null
   // todo change to be thread safe on jvm
   private val results = mutableMapOf<TestCase, TestResult?>()

   fun toMap(): Map<TestCase, TestResult> {
      @Suppress("UNCHECKED_CAST")
      return results.toMap() as Map<TestCase, TestResult>
   }

   // marks a test as started
   fun started(testCase: TestCase) {
      results[testCase] = null
   }

   // marks a test as completed
   fun completed(testCase: TestCase, result: TestResult) {
      results[testCase] = result
   }

   fun isStarted(testCase: TestCase): Boolean {
      return results.contains(testCase)
   }

   /**
    * Returns true if any test has failed.
    */
   fun hasErrorOrFailure(): Boolean {
      return results.values.any { it?.isErrorOrFailure == true  }
   }

   /**
    * Returns true if for a given test case, all started child tests have been completed.
    */
   fun isSubtreeComplete(testCase: TestCase): Boolean {
      return children(testCase).all { result(testCase) != null }
   }

   fun roots(): Map<TestCase, TestResult?> {
      return results.filter { it.key.isRootTest() }
   }

   fun rootsCompleted(): Boolean {
      return results.none { it.key.isRootTest() && it.value == null }
   }

   private fun result(testCase: TestCase): TestResult? {
      return results.toList().firstOrNull { it.first.descriptor == testCase.descriptor }?.second
   }

   /**
    * Returns all the immediate children of the given test case.
    */
   fun children(testCase: TestCase): Set<TestCase> {
      return results.filter { it.key.descriptor.isChildOf(testCase.descriptor) }.keys
   }
}
