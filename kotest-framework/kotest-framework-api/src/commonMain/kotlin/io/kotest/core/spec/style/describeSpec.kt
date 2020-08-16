package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.spec.style.scopes.DescribeSpecRootScope
import io.kotest.core.spec.style.scopes.Lifecycle
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.test.TestCaseConfig

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [DescribeSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'describe-spec' style.
 */
fun describeSpec(block: DescribeSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = DescribeSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class DescribeSpecTestFactoryConfiguration : TestFactoryConfiguration(), DescribeSpecRootScope {
   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
}

abstract class DescribeSpec(body: DescribeSpec.() -> Unit = {}) : DslDrivenSpec(), DescribeSpecRootScope {

   init {
      body()
   }

   override fun lifecycle(): Lifecycle = Lifecycle.from(this)
   override fun defaultConfig(): TestCaseConfig = resolvedDefaultConfig()
   override fun registration(): RootTestRegistration = RootTestRegistration.from(this)
}
