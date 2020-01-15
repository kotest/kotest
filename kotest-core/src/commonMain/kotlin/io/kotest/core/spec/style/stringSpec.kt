package io.kotest.core.spec.style

import io.kotest.core.config.Project
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.test.TestCaseConfig

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
   override fun defaultConfig(): TestCaseConfig = defaultTestCaseConfig ?: Project.testCaseConfig()
   override val addTest = ::addDynamicTest
}

abstract class StringSpec(body: StringSpec.() -> Unit = {}) : SpecConfiguration(), StringSpecDsl {
   override fun defaultConfig(): TestCaseConfig =
      defaultTestCaseConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()

   override val addTest = ::addRootTestCase

   init {
      body()
   }
}
