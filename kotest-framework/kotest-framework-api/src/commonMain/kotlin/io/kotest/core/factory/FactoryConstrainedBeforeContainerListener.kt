package io.kotest.core.factory

import io.kotest.core.listeners.AfterContainerListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

class FactoryConstrainedBeforeContainerListener(
   private val factoryId: FactoryId,
   private val delegate: BeforeContainerListener,
) : BeforeContainerListener {

   override suspend fun beforeContainer(testCase: TestCase) {
      if (testCase.factoryId == factoryId) {
         delegate.beforeContainer(testCase)
      }
   }
}

class FactoryConstrainedAfterContainerListener(
   private val factoryId: FactoryId,
   private val delegate: AfterContainerListener,
) : AfterContainerListener {

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      if (testCase.factoryId == factoryId) {
         delegate.afterContainer(testCase, result)
      }
   }
}

class FactoryConstrainedBeforeTestListener(
   private val factoryId: FactoryId,
   private val delegate: BeforeTestListener,
) : BeforeTestListener {

   override suspend fun beforeTest(testCase: TestCase) {
      if (testCase.factoryId == factoryId) {
         delegate.beforeTest(testCase)
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      if (testCase.factoryId == factoryId) {
         delegate.beforeAny(testCase)
      }
   }
}

class FactoryConstrainedAfterTestListener(
   private val factoryId: FactoryId,
   private val delegate: AfterTestListener,
) : AfterTestListener {

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      if (testCase.factoryId == factoryId) {
         delegate.afterTest(testCase, result)
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      if (testCase.factoryId == factoryId) {
         delegate.afterAny(testCase, result)
      }
   }
}
