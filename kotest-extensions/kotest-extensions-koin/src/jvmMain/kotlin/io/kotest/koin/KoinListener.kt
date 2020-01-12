package io.kotest.koin

import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.extensions.TestListener
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

class KoinListener(
   private val modules: List<Module>
) : TestListener {

   constructor(module: Module) : this(listOf(module))

   override suspend fun beforeTest(testCase: TestCase) {
      startKoin {
         modules(modules)
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      stopKoin()
   }
}
