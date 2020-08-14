package io.kotest.koin

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.test.mock.MockProvider
import org.koin.test.mock.Provider

class KoinListener(
   private val modules: List<Module>,
   private val mockProvider: Provider<*>? = null
) : TestListener {

   constructor(module: Module, mockProvider: Provider<*>? = null) : this(listOf(module), mockProvider)

   override suspend fun beforeAny(testCase: TestCase) {
      startKoin {
         if(mockProvider != null) MockProvider.register(mockProvider)
         modules(modules)
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      stopKoin()
   }
}
