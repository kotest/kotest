package io.kotest.core.spec.style

import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.engine.config.Project
import io.kotest.engine.factory.generateTests
import io.kotest.engine.spec.AbstractSpec
import io.kotest.engine.spec.createTestCase
import io.kotest.engine.test.toDescription

abstract class DslDrivenSpec : AbstractSpec() {

   /**
    * Contains the root [TestCase]s used in this spec.
    */
   private var rootTestCases = emptyList<TestCase>()

   override fun materializeRootTests(): List<TestCase> {
      return rootTestCases + factories.flatMap { it.generateTests(this::class.toDescription(), this) }
   }

   /**
    * Adds a new root-level [TestCase] to this [AbstractSpec].
    */
   internal fun addRootTestCase(
      name: DescriptionName.TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      require(rootTestCases.none { it.description.name == name }) { "Cannot add test with duplicate name $name" }
      //require(acceptingTopLevelRegistration) { "Cannot add nested test here. Please see documentation on testing styles for how to layout nested tests correctly" }
      rootTestCases = rootTestCases + createTestCase(name, test, config, type)
   }

   /**
    * Returns the [TestCaseConfig] to be used by this spec, taking into account overrides of the var,
    * the function version, and finally project defaults.
    */
   internal fun resolvedDefaultConfig(): TestCaseConfig =
      defaultTestConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()
}
