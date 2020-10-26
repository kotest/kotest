package io.kotest.core.factory

/**
 * Builds an immutable [TestFactory] from this [TestFactoryConfiguration].
 */
internal fun TestFactoryConfiguration.build(): TestFactory {
   return TestFactory(
      factoryId = factoryId,
      tests = tests,
      tags = _tags,
      listeners = _listeners.map { FactorySpecificTestListener(factoryId, it) },
      extensions = _extensions,
      assertionMode = assertions,
   )
}
