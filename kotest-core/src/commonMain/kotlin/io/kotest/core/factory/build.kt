package io.kotest.core.factory

import io.kotest.core.Tuple2
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Builds an immutable [TestFactory] from this [TestFactoryConfiguration].
 */
fun TestFactoryConfiguration.build(): TestFactory {

   val factory = TestFactory(
      factoryId = TestFactoryId.next(),
      tests = this.tests,
      tags = this._tags,
      listeners = this._listeners,
      extensions = this._extensions,
      assertionMode = this.assertions,
      factories = this.factories
   )

   val callbacks = object : TestListener {
      override suspend fun beforeTest(testCase: TestCase) {
         if (testCase.factoryId == factory.factoryId) {
            this@build.beforeTests.forEach { it(testCase) }
         }
      }

      override suspend fun afterTest(testCase: TestCase, result: TestResult) {
         if (testCase.factoryId == factory.factoryId) {
            this@build.afterTests.forEach { it(Tuple2(testCase, result)) }
         }
      }

      override suspend fun beforeContainer(testCase: TestCase) {
         if (testCase.factoryId == factory.factoryId) {
            this@build.beforeContainers.forEach { it(testCase) }
         }
      }

      override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
         if (testCase.factoryId == factory.factoryId) {
            this@build.afterContainers.forEach { it(Tuple2(testCase, result)) }
         }
      }

      override suspend fun beforeEach(testCase: TestCase) {
         if (testCase.factoryId == factory.factoryId) {
            this@build.beforeEaches.forEach { it(testCase) }
         }
      }

      override suspend fun afterEach(testCase: TestCase, result: TestResult) {
         if (testCase.factoryId == factory.factoryId) {
            this@build.afterEaches.forEach { it(Tuple2(testCase, result)) }
         }
      }

      override suspend fun beforeAny(testCase: TestCase) {
         if (testCase.factoryId == factory.factoryId) {
            this@build.beforeAnys.forEach { it(testCase) }
         }
      }

      override suspend fun afterAny(testCase: TestCase, result: TestResult) {
         if (testCase.factoryId == factory.factoryId) {
            this@build.afterAnys.forEach { it(Tuple2(testCase, result)) }
         }
      }
   }

   return factory.copy(listeners = factory.listeners + callbacks)
}
