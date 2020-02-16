package io.kotest.koin

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

@AutoScan
class KoinListener(private val modules: List<Module>) : TestListener {

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
