package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.style.scopes.ExpectSpecScope
import io.kotest.core.spec.style.scopes.Lifecycle
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.test.TestCaseConfig

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [ExpectSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'expect-spec' style.
 */
fun expectSpec(block: ExpectSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = ExpectSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class ExpectSpecTestFactoryConfiguration : TestFactoryConfiguration(), ExpectSpecScope {
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
}

abstract class ExpectSpec(body: ExpectSpec.() -> Unit = {}) : DslDrivenSpec(), ExpectSpecScope {

   init {
      body()
   }

   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
}
