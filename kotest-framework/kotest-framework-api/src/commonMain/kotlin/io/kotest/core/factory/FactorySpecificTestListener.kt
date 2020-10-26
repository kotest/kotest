package io.kotest.core.factory

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Wraps an existing [TestListener] and forwards calls only if the [TestCase] in question was
 * defined in the given factory.
 */
class FactorySpecificTestListener(
   private val factoryId: FactoryId,
   private val delegate: TestListener
) : TestListener {

   override suspend fun beforeTest(testCase: TestCase) {
      if (testCase.factoryId == factoryId) {
         delegate.beforeTest(testCase)
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      if (testCase.factoryId == factoryId) {
         delegate.afterTest(testCase, result)
      }
   }

   override suspend fun beforeContainer(testCase: TestCase) {
      if (testCase.factoryId == factoryId) {
         delegate.beforeContainer(testCase)
      }
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      if (testCase.factoryId == factoryId) {
         delegate.afterContainer(testCase, result)
      }
   }

   override suspend fun beforeEach(testCase: TestCase) {
      if (testCase.factoryId == factoryId) {
         delegate.beforeEach(testCase)
      }
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      if (testCase.factoryId == factoryId) {
         delegate.afterEach(testCase, result)
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      if (testCase.factoryId == factoryId) {
         delegate.beforeAny(testCase)
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      if (testCase.factoryId == factoryId) {
         delegate.afterAny(testCase, result)
      }
   }
}
