package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.spec.style.scopes.FunSpecRootScope
import io.kotest.core.spec.style.scopes.Lifecycle
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.test.TestCaseConfig

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

class FunSpecTestFactoryConfiguration : TestFactoryConfiguration(), FunSpecRootScope {
   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
}

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : DslDrivenSpec(), FunSpecRootScope {

   init {
      body()
   }

   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)

}
