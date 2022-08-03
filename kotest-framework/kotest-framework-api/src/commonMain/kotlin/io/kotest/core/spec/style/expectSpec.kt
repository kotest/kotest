package io.kotest.core.spec.style

import io.kotest.common.runBlocking
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.scopes.ExpectSpecRootScope

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

class ExpectSpecTestFactoryConfiguration : TestFactoryConfiguration(), ExpectSpecRootScope

abstract class ExpectSpec(body: suspend ExpectSpec.() -> Unit = {}) : DslDrivenSpec(), ExpectSpecRootScope
