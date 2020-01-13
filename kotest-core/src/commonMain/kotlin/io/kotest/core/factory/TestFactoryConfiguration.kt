package io.kotest.core.factory

import io.kotest.core.sourceRef
import io.kotest.core.spec.TestConfiguration
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

/**
 * A [TestFactoryConfiguration] provides a DSL to allow for easy creation of a
 * [TestFactory] when this class is the receiver of a lambda parameter.
 *
 * This class shouldn't be used directly, but as the base for a particular
 * layout style, eg [FunSpecTestFactoryConfiguration].
 */
abstract class TestFactoryConfiguration : TestConfiguration() {

   /**
    * Contains the [DynamicTest]s that have been added to this configuration.
    */
   internal var tests = emptyList<DynamicTest>()

   /**
    * Adds a new [DynamicTest] to this factory. When this factory is included
    * into a [SpecConfiguration] these tests will be added to the spec as root [TestCase]s.
    */
   protected fun addDynamicTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      require(tests.none { it.name == name }) { "Cannot add test with duplicate name $name" }
      require(name.isNotBlank() && name.isNotEmpty()) { "Cannot add test with blank or empty name" }
      this.tests = this.tests + DynamicTest(
         name,
         test,
         config,
         type,
         sourceRef()
      )
   }
}
