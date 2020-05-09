package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.style.scopes.GivenScope
import io.kotest.core.spec.style.scopes.ScopeContext
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName

@Suppress("FunctionName")
interface BehaviorSpecMethods {

   fun addRootTest(name: String, test: suspend TestContext.() -> Unit, config: TestCaseConfig, type: TestType)

   private fun addContainerTest(name: String, enabled: Boolean, test: suspend TestContext.() -> Unit) {
      val config = if (enabled) defaultConfig() else defaultConfig().copy(enabled = false)
      addRootTest(name, test, config, TestType.Container)
   }

   fun description(name: String): Description = Description.specUnsafe(this).append(name)

   fun defaultConfig(): TestCaseConfig

   fun lifecycle(): ScopeContext

   /**
    * Adds a top level [GivenScope] to this spec.
    */
   fun Given(name: String, test: suspend GivenScope.() -> Unit) = addGiven(name, test, true)

   /**
    * Adds a top level [GivenScope] to this spec.
    */
   fun given(name: String, test: suspend GivenScope.() -> Unit) = addGiven(name, test, true)

   /**
    * Adds a top level disabled [GivenScope] to this spec.
    */
   fun xgiven(name: String, test: suspend GivenScope.() -> Unit) = addGiven(name, test, false)

   private fun addGiven(name: String, test: suspend GivenScope.() -> Unit, enabled: Boolean) {
      val testName = createTestName("Given: ", name)
      addContainerTest(testName, enabled) {
         GivenScope(description(testName), lifecycle()).test()
      }
   }
}

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [BehaviorSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'behavior-spec' style.
 */
fun behaviorSpec(block: BehaviorSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = BehaviorSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class BehaviorSpecTestFactoryConfiguration : TestFactoryConfiguration(), BehaviorSpecMethods {

   override fun addRootTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) = addDynamicTest(name, test, config, type)

   override fun lifecycle(): ScopeContext = ScopeContext.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
}

abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : DslDrivenSpec(), BehaviorSpecMethods {

   init {
      body()
   }

   override fun addRootTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) = addRootTestCase(name, test, config, type)

   override fun lifecycle(): ScopeContext = ScopeContext.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
}
