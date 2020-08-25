package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.spec.style.scopes.BehaviorSpecRootScope
import io.kotest.core.spec.style.scopes.Lifecycle
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.test.TestCaseConfig

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

class BehaviorSpecTestFactoryConfiguration : TestFactoryConfiguration(), BehaviorSpecRootScope {
   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
}

abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : DslDrivenSpec(), BehaviorSpecRootScope {

   init {
      body()
   }

   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
}
