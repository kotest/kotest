package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.scopes.FeatureSpecRootScope

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

class FeatureSpecTestFactoryConfiguration : TestFactoryConfiguration(), FeatureSpecRootScope

abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : DslDrivenSpec(), FeatureSpecRootScope {
   init {
      body()
   }
}
