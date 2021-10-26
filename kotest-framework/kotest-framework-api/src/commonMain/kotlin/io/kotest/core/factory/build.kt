package io.kotest.core.factory

import io.kotest.core.listeners.TestListener

/**
 * Builds an immutable [TestFactory] from this [TestFactoryConfiguration].
 */
internal fun TestFactoryConfiguration.build(): TestFactory {
   return TestFactory(
      factoryId = factoryId,
      tags = _tags,
      extensions = _extensions.map {
         when (it) {
            is TestListener -> FactoryConstrainedTestListener(factoryId, it)
            else -> it
         }
      },
      assertionMode = assertions,
      tests = tests
   )
}
