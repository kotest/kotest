package io.kotest.core.spec.style

import io.kotest.core.factory.generate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.createTestCase
import io.kotest.core.spec.description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

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
   protected fun addRootTestCase(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      require(rootTestCases.none { it.name == name }) { "Cannot add test with duplicate name $name" }
      require(name.isNotBlank() && name.isNotEmpty()) { "Cannot add test with blank or empty name" }
      //require(acceptingTopLevelRegistration) { "Cannot add nested test here. Please see documentation on testing styles for how to layout nested tests correctly" }
      rootTestCases = rootTestCases + createTestCase(name, test, config, type)
   }
}
