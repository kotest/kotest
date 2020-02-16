package io.kotest.core.spec.style

import io.kotest.core.config.Project
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCaseConfig

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

class BehaviorSpecTestFactoryConfiguration : TestFactoryConfiguration(), BehaviorSpecDsl {
   override fun defaultConfig(): TestCaseConfig = defaultTestConfig ?: Project.testCaseConfig()
   override val addTest = ::addDynamicTest
}

abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : Spec(), BehaviorSpecDsl {
   override fun defaultConfig(): TestCaseConfig =
      defaultTestConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()

   override val addTest = ::addRootTestCase

   init {
      body()
   }
}
