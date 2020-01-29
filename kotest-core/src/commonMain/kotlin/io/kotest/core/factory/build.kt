package io.kotest.core.factory

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.listeners.TestListener
import io.kotest.fp.Tuple2

/**
 * Builds an immutable [TestFactory] from this configuration.
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

      override suspend fun afterSpec(spec: Spec) {
         this@build.afterSpecs.forEach { it(spec) }
      }

      override suspend fun beforeSpec(spec: Spec) {
         this@build.beforeSpecs.forEach { it(spec) }
      }
   }

   return factory.copy(listeners = factory.listeners + callbacks)
}
