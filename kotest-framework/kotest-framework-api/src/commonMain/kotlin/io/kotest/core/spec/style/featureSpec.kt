package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.spec.style.scopes.FeatureSpecRootScope
import io.kotest.core.spec.style.scopes.Lifecycle
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.test.TestCaseConfig

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [FeatureSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'feature-spec' style.
 */
fun featureSpec(block: FeatureSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = FeatureSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class FeatureSpecTestFactoryConfiguration : TestFactoryConfiguration(), FeatureSpecRootScope {
   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
}

abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : DslDrivenSpec(), FeatureSpecRootScope {

   init {
      body()
   }

   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = this.resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)

}
