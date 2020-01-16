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
 * The receiver of the block is a [DescribeSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'describe-spec' style.
 */
fun describeSpec(block: DescribeSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = DescribeSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class DescribeSpecTestFactoryConfiguration : TestFactoryConfiguration(), DescribeSpecDsl {
   override fun defaultConfig(): TestCaseConfig = defaultTestCaseConfig ?: Project.testCaseConfig()
   override val addTest = ::addDynamicTest
}

abstract class DescribeSpec(body: DescribeSpec.() -> Unit = {}) : SpecConfiguration(), DescribeSpecDsl {
   override fun defaultConfig(): TestCaseConfig =
      defaultTestCaseConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()

   override val addTest = ::addRootTestCase

   init {
      body()
   }
}
