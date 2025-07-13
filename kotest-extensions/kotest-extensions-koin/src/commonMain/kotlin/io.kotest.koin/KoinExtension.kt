package io.kotest.koin

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.isRootTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.test.mock.MockProvider
import org.koin.test.mock.Provider

/**
 * Determines how the koin test context lifecycle is mapped to test cases.
 *
 * [KoinLifecycleMode.Root] will setup and teardown the test context before and after root tests only.
 * [KoinLifecycleMode.Test] will setup and teardown the test context only at leaf tests.
 *
 */
enum class KoinLifecycleMode {
   Root, Test
}

class KoinExtension(
   private val modules: List<Module>,
   private val mockProvider: Provider<*>? = null,
   private val mode: KoinLifecycleMode
) : TestCaseExtension {

   constructor(
      module: Module,
      mockProvider: Provider<*>? = null,
   ) : this(listOf(module), mockProvider, KoinLifecycleMode.Test)

   constructor(
      module: Module,
      mockProvider: Provider<*>? = null,
      mode: KoinLifecycleMode
   ) : this(listOf(module), mockProvider, mode)

   constructor(
      modules: List<Module>,
      mockProvider: Provider<*>? = null,
   ) : this(modules, mockProvider, KoinLifecycleMode.Test)

   private fun TestCase.isApplicable() = (mode == KoinLifecycleMode.Root && this.isRootTest()) ||
      (mode == KoinLifecycleMode.Test && type == TestType.Test)

   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      return if (testCase.isApplicable()) {
         try {
            stopKoin()
            startKoin {
               if (mockProvider != null) MockProvider.register(mockProvider)
               modules(modules)
            }
            execute(testCase)
         } catch (t: Throwable) {
            throw t
         } finally {
            stopKoin()
         }
      } else {
         execute(testCase)
      }
   }
}
