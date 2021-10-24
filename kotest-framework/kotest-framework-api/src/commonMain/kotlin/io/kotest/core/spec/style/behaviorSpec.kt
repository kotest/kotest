package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecRootContext

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

class BehaviorSpecTestFactoryConfiguration : TestFactoryConfiguration(), BehaviorSpecRootContext {
}

abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : DslDrivenSpec(), BehaviorSpecRootContext {
   init {
      body()
   }
}
