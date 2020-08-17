package io.kotest.core.factory

import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.TestConfiguration
import io.kotest.core.config.configuration
import io.kotest.core.sourceRef
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

/**
 * A [TestFactoryConfiguration] extends [TestConfiguration] with the ability to register
 * [DynamicTest]s. This class shouldn't be used directly, but as the base for a particular
 * layout style, eg [FunSpecTestFactoryConfiguration].
 */
abstract class TestFactoryConfiguration : TestConfiguration() {

   /**
    * This [factoryId] is a unique id across all factories. The id is used by
    * lifecycle callbacks declared in this factory to ensure they only operate
    * on tests declared in this factory.
    */
   val factoryId: FactoryId = FactoryId.next()

   /**
    * Contains the [DynamicTest]s that have been added to this configuration.
    */
   internal var tests = emptyList<DynamicTest>()

   private fun addDynamicTest(test: DynamicTest) {
      if (tests.any { it.name == test.name }) throw DuplicatedTestNameException(test.name)
      this.tests = this.tests + test
   }

   /**
    * Include the tests, listeners and extensions from the given [TestFactory] in this factory.
    * Tests are added in order from where this include was invoked using configuration and
    * settings at the time the method was invoked.
    */
   fun include(factory: TestFactory) {
      factory.tests.forEach { addDynamicTest(it) }
   }

   internal fun resolvedDefaultConfig(): TestCaseConfig = defaultTestConfig ?: configuration.defaultTestConfig

   /**
    * Adds a new [DynamicTest] to this factory. When this factory is included
    * into a [Spec] these tests will be materialized as [TestCase]s.
    */
   override fun addTest(
      name: DescriptionName.TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType,
   ) = addDynamicTest(DynamicTest(name, test, config, type, sourceRef(), factoryId))
}
