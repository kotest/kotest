package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.scopes.FreeSpecRootScope

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

class FreeSpecTestFactoryConfiguration : TestFactoryConfiguration(), FreeSpecRootScope

abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) : DslDrivenSpec(), FreeSpecRootScope {
   init {
      body()
   }
}
