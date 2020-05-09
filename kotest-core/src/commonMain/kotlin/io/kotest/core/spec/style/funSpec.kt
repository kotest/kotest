package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.style.scopes.FunSpecContextScope
import io.kotest.core.spec.style.scopes.ScopeContext
import io.kotest.core.spec.style.scopes.RootTestWithConfigBuilder
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [FunSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'fun-spec' style.
 */
fun funSpec(block: FunSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = FunSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

interface FunSpecMethods {

   fun addRootTest(name: String, test: suspend TestContext.() -> Unit, config: TestCaseConfig, type: TestType)

   fun description(name: String): Description = Description.specUnsafe(this).append(name)

   fun defaultConfig(): TestCaseConfig

   fun lifecycle(): ScopeContext

   fun registration(): RootTestRegistration

   /**
    * Adds a top level [FunSpecContextScope] to the spec.
    */
   fun context(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      addRootTest(
         name,
         { FunSpecContextScope(description(name), lifecycle()).test() },
         defaultConfig(),
         TestType.Container
      )
   }

   fun test(name: String): RootTestWithConfigBuilder = RootTestWithConfigBuilder(name, registration())

   /**
    * Adds a top level test, with the given name and test function, using the
    * resolved default test config.
    */
   fun test(name: String, test: suspend TestContext.() -> Unit) =
      addRootTest(name, test, defaultConfig(), TestType.Test)
}

class FunSpecTestFactoryConfiguration : TestFactoryConfiguration(), FunSpecMethods {

   override fun addRootTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) = addDynamicTest(name, test, config, type)

   override fun lifecycle(): ScopeContext = ScopeContext.from(this)
   override fun registration(): RootTestRegistration = RootTestRegistration.Companion.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
}

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : DslDrivenSpec(), FunSpecMethods {

   init {
      body()
   }

   override fun lifecycle(): ScopeContext = ScopeContext.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.Companion.from(this)

   override fun addRootTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) = addRootTestCase(name, test, config, type)
}
