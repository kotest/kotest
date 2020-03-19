package io.kotest.core.spec.style

import io.kotest.core.config.Project
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.test.TestCaseConfig

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [FunSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'fun-spec' style.
 */
fun funSpec(block: FunSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = FunSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class FunSpecTestFactoryConfiguration : TestFactoryConfiguration(), FunSpecDsl {
   override fun defaultConfig(): TestCaseConfig = defaultTestConfig ?: Project.testCaseConfig()
   override val addTest = ::addDynamicTest
}

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : DslDrivenSpec(), FunSpecDsl {

   override fun defaultConfig(): TestCaseConfig =
      defaultTestConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()

   override val addTest = ::addRootTestCase

   init {
      body()
   }
}
