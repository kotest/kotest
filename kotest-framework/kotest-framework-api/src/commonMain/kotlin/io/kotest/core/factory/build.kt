package io.kotest.core.factory

import io.kotest.core.listeners.AfterContainerListener
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.listeners.BeforeTestListener

/**
 * Builds an immutable [TestFactory] from this [TestFactoryConfiguration].
 */
internal fun TestFactoryConfiguration.build(): TestFactory {
   return TestFactory(
      factoryId = factoryId,
      tags = _tags,
      extensions = _extensions.map {
         when (it) {
            is BeforeEachListener -> FactoryConstrainedBeforeEachListener(factoryId, it)
            is AfterEachListener -> FactoryConstrainedAfterEachListener(factoryId, it)
            is BeforeContainerListener -> FactoryConstrainedBeforeContainerListener(factoryId, it)
            is AfterContainerListener -> FactoryConstrainedAfterContainerListener(factoryId, it)
            is BeforeTestListener -> FactoryConstrainedBeforeTestListener(factoryId, it)
            is AfterTestListener -> FactoryConstrainedAfterTestListener(factoryId, it)
            else -> it
         }
      },
      assertionMode = assertions,
      tests = tests,
      configuration = this
   )
}
