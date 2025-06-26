package io.kotest.koin

import io.kotest.property.PropTestListener
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.test.mock.MockProvider
import org.koin.test.mock.Provider

/**
 * A Kotest [PropTestListener] that will start and stop a Koin application
 * for each iteration of a property test.
 *
 * This listener can be used to provide a clean Koin context for each
 * execution of a property test.
 *
 * @param modules the modules to pass to Koin.
 * @param mockProvider the mock provider to use.
 */
class KoinPropTestListener(
   private val modules: List<Module>,
   private val mockProvider: Provider<*>? = null,
) : PropTestListener {

   constructor(
      module: Module,
      mockProvider: Provider<*>? = null,
   ) : this(listOf(module), mockProvider)

   override suspend fun beforeTest() {
      stopKoin() // ensure no context is running
      startKoin {
         if (mockProvider != null) MockProvider.register(mockProvider)
         modules(modules)
      }
   }

   override suspend fun afterTest() {
      stopKoin()
   }
}
