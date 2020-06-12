package io.kotest.core.spec.style

import io.kotest.core.config.Project
import io.kotest.core.factory.generate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.createTestCase
import io.kotest.core.spec.description
import io.kotest.core.test.*

abstract class DslDrivenSpec : Spec() {

   /**
    * Contains the root [TestCase]s used in this spec.
    */
   private var rootTestCases = emptyList<TestCase>()

   override fun materializeRootTests(): List<TestCase> {
      return rootTestCases + factories.flatMap { it.generate(this::class.description(), this) }
   }

   /**
    * Adds a new root-level [TestCase] to this [Spec].
    */
   internal fun addRootTestCase(
      name: TestName,
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
