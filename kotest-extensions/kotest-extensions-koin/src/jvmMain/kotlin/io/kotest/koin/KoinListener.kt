package io.kotest.koin

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import org.koin.core.context.KoinContextHandler
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

   override suspend fun beforeTest(testCase: TestCase) {
      startKoinIfNotStarted()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      stopKoin()
   }

   override suspend fun beforeContainer(testCase: TestCase) {
      if (testCase.type == TestType.Container) startKoinIfNotStarted()
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      if (testCase.type == TestType.Container) stopKoin()
   }

   override suspend fun beforeEach(testCase: TestCase) {
      if (testCase.type == TestType.Test) startKoinIfNotStarted()
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      if (testCase.type == TestType.Test) stopKoin()
   }

   override suspend fun beforeAny(testCase: TestCase) {
      startKoinIfNotStarted()
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      stopKoin()
   }

   private fun startKoinIfNotStarted() {
      KoinContextHandler.getOrNull() ?: startKoin {
         if(mockProvider != null) MockProvider.register(mockProvider)
         modules(modules)
      }
   }
}
