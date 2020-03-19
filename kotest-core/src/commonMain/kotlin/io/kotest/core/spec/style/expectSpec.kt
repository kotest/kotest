package io.kotest.core.spec.style

import io.kotest.core.config.Project
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
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

class ExpectSpecTestFactoryConfiguration : TestFactoryConfiguration(), ExpectSpecDsl {
   override fun defaultConfig(): TestCaseConfig = defaultTestConfig ?: Project.testCaseConfig()
   override val addTest = ::addDynamicTest
}

abstract class ExpectSpec(body: ExpectSpec.() -> Unit = {}) : DslDrivenSpec(), ExpectSpecDsl {
   override fun defaultConfig(): TestCaseConfig =
      defaultTestConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()

   override val addTest = ::addRootTestCase

   init {
      body()
   }
}
