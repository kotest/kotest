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
 * The receiver of the block is a [FeatureSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'feature-spec' style.
 */
fun featureSpec(block: FeatureSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = FeatureSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class FeatureSpecTestFactoryConfiguration : TestFactoryConfiguration(), FeatureSpecDsl {
   override fun defaultConfig(): TestCaseConfig = defaultTestCaseConfig ?: Project.testCaseConfig()
   override val addTest = ::addDynamicTest
}

abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : SpecConfiguration(), FeatureSpecDsl {
   override fun defaultConfig(): TestCaseConfig =
      defaultTestCaseConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()

   override val addTest = ::addRootTestCase

   init {
      body()
   }
}
