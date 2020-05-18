package io.kotest.core.factory

import io.kotest.core.config.Project
import io.kotest.core.sourceRef
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeTest
import io.kotest.core.spec.TestConfiguration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
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

   internal fun resolvedDefaultConfig(): TestCaseConfig = defaultTestConfig ?: Project.testCaseConfig()

   // test lifecycle callbacks
   internal var beforeTests = emptyList<BeforeTest>()
   internal var afterTests = emptyList<AfterTest>()

   /**
    * Registers a new before-test callback to be executed before every [TestCase].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   override fun beforeTest(f: BeforeTest) {
      beforeTests = beforeTests + f
   }

   /**
    * Registers a new after-test callback to be executed after every [TestCase].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   override fun afterTest(f: AfterTest) {
      afterTests = afterTests + f
   }

   /**
    * Adds a new [DynamicTest] to this factory. When this factory is included
    * into a [Spec] these tests will be added to the spec as root [TestCase]s.
    */
   internal fun addDynamicTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      require(tests.none { it.name == name }) { "Cannot add test with duplicate name $name" }
      require(name.isNotBlank() && name.isNotEmpty()) { "Cannot add test with blank or empty name" }
      this.tests = this.tests + DynamicTest(name.normalizedTestName(), test, config, type, sourceRef())
   }
}

fun String.normalizedTestName() = this.trim().replace("\n", "")
