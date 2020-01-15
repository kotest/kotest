package io.kotest.core.spec.style

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.SpecConfiguration

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [StringSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'string-spec' style.
 */
fun stringSpec(block: StringSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = StringSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

/**
 * Decorates a [TestFactoryConfiguration] with the StringSpec DSL.
 */
class StringSpecTestFactoryConfiguration : TestFactoryConfiguration(), StringSpecDsl {
   override val addTest = ::addDynamicTest
}

abstract class StringSpec(body: StringSpec.() -> Unit = {}) : SpecConfiguration(), StringSpecDsl {
   override val addTest = ::addRootTestCase

   init {
      body()
   }
}
