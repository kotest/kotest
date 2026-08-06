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

/**
 * A Kotest [TestCaseExtension] that starts and stops a Koin application around tests.
 *
 * Modules can be supplied either as pre-built [Module] instances, or as a [modulesFactory]
 * lambda that is invoked once per Koin start to build fresh module instances.
 *
 * Prefer the factory form if you mutate the Koin context during a test (for example with
 * `KoinTest.declare`). Reusing the same [Module] instance across tests can leak singleton state
 * between tests, because Koin caches instances on the factories held by the module
 * (see https://github.com/kotest/kotest/issues/6006 and https://github.com/InsertKoinIO/koin/issues/2412).
 * Building fresh modules for each test sidesteps that by giving every test a clean module instance:
 *
 * ```
 * extension(KoinExtension { listOf(myModule()) })
 * ```
 */
class KoinExtension private constructor(
   private val modulesFactory: () -> List<Module>,
   private val mockProvider: Provider<*>? = null,
   private val mode: KoinLifecycleMode,
) : TestCaseExtension {

   constructor(
      modules: List<Module>,
      mockProvider: Provider<*>? = null,
      mode: KoinLifecycleMode,
   ) : this({ modules }, mockProvider, mode)

   constructor(
      module: Module,
      mockProvider: Provider<*>? = null,
   ) : this({ listOf(module) }, mockProvider, KoinLifecycleMode.Test)

   constructor(
      module: Module,
      mockProvider: Provider<*>? = null,
      mode: KoinLifecycleMode,
   ) : this({ listOf(module) }, mockProvider, mode)

   constructor(
      modules: List<Module>,
      mockProvider: Provider<*>? = null,
   ) : this({ modules }, mockProvider, KoinLifecycleMode.Test)

   /**
    * Creates an extension that builds fresh modules for each Koin start by invoking
    * [modulesFactory]. Use this if you modify the Koin context during a test (e.g. with
    * `KoinTest.declare`) to avoid leaking singleton state between tests.
    */
   constructor(
      mode: KoinLifecycleMode = KoinLifecycleMode.Test,
      mockProvider: Provider<*>? = null,
      modulesFactory: () -> List<Module>,
   ) : this(modulesFactory, mockProvider, mode)

   private fun TestCase.isApplicable() = (mode == KoinLifecycleMode.Root && this.isRootTest()) ||
      (mode == KoinLifecycleMode.Test && type == TestType.Test)

   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      return if (testCase.isApplicable()) {
         try {
            stopKoin()
            startKoin {
               if (mockProvider != null) MockProvider.register(mockProvider)
               modules(modulesFactory())
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
