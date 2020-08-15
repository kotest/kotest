package io.kotest.core.spec

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.createTestCases
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createRootTestCase
import kotlin.js.JsName

/**
 * Base class for specs that allow for registration of tests via the DSL.
 */
abstract class DslDrivenSpec : BaseSpec() {

   /**
    * Contains the root [TestCase]s used in this spec.
    */
   private var rootTestCases = emptyList<TestCase>()

   override fun materializeRootTests(): List<RootTest> {
      return rootTestCases.withIndex().map { RootTest(it.value, it.index) }
   }

   override fun include(factory: TestFactory) {
      rootTestCases = rootTestCases + factory.createTestCases(this::class.toDescription(), this)
   }

   /**
    * Adds a new root-level [TestCase] to this [Spec].
    */
   internal fun addRootTestCase(
      name: DescriptionName.TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      require(rootTestCases.none { it.description.name == name }) { "Cannot add test with duplicate name $name" }
      //require(acceptingTopLevelRegistration) { "Cannot add nested test here. Please see documentation on testing styles for how to layout nested tests correctly" }
      rootTestCases = rootTestCases + createRootTestCase(this, name, test, config, type)
   }
}
