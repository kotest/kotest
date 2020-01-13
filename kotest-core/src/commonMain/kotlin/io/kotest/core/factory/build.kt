package io.kotest.core.factory

import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.TestListener
import io.kotest.fp.Tuple2

/**
 * Builds an immutable [TestFactory] from this configuration.
 */
fun TestFactoryConfiguration.build(): TestFactory {

   val factory = TestFactory(
      factoryId = TestFactoryId.next(),
      tests = this.tests,
      tags = this.tags,
      listeners = this.listeners,
      extensions = this.extensions,
      assertionMode = this.assertionMode,
      factories = this.factories
   )

   val callbacks = object : TestListener {

      override suspend fun beforeTest(testCase: TestCase) {
         println("${testCase.factoryId} == ${factory.factoryId} is ${testCase.factoryId == factory.factoryId}")
         if (testCase.factoryId == factory.factoryId) {
            this@build.beforeTests.forEach { it(testCase) }
         }
      }

      override suspend fun afterTest(testCase: TestCase, result: TestResult) {
         if (testCase.factoryId == factory.factoryId) {
            this@build.afterTests.forEach { it(Tuple2(testCase, result)) }
         }
      }

      override fun afterSpec(spec: SpecConfiguration) {
         this@build.afterSpecs.forEach { it() }
      }

      override fun beforeSpec(spec: SpecConfiguration) {
         this@build.beforeSpecs.forEach { it() }
      }
   }

   return factory.copy(listeners = factory.listeners + callbacks)
}
