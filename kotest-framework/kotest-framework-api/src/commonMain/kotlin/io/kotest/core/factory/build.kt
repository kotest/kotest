package io.kotest.core.factory

/**
 * Builds an immutable [TestFactory] from this [TestFactoryConfiguration].
 */
internal fun TestFactoryConfiguration.build(): TestFactory {

   val factory = TestFactory(
      factoryId = factoryId,
      tests = this.tests,
      tags = _tags,
      listeners = _listeners.map { FactorySpecificTestListener(factoryId, it) },
      extensions = _extensions,
      assertionMode = assertions,
   )

   return factory
}
