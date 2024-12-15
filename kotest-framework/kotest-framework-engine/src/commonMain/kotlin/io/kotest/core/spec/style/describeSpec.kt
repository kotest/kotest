package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.scopes.DescribeSpecRootScope

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

class DescribeSpecTestFactoryConfiguration : TestFactoryConfiguration(), DescribeSpecRootScope

abstract class DescribeSpec(body: DescribeSpec.() -> Unit = {}) : DslDrivenSpec(), DescribeSpecRootScope {
   init {
      body()
   }
}
