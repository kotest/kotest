package io.kotlintest.koin

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

class KoinListener(
        private val modules: List<Module>
) : TestListener {

  constructor(module: Module) : this(listOf(module))

  override fun beforeTest(testCase: TestCase) {
    startKoin {
      modules(modules)
    }
  }

  override fun afterTest(testCase: TestCase, result: TestResult) {
    stopKoin()
  }
}