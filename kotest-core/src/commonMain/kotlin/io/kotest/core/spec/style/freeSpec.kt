package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.style.scopes.FreeSpecScope
import io.kotest.core.spec.style.scopes.Lifecycle
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.test.TestCaseConfig

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [FreeSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'free-spec' style.
 */
fun freeSpec(block: FreeSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = FreeSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class FreeSpecTestFactoryConfiguration : TestFactoryConfiguration(),   FreeSpecScope {
   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
}

abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) : DslDrivenSpec(), FreeSpecScope {

   init {
      body()
   }

   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
}
