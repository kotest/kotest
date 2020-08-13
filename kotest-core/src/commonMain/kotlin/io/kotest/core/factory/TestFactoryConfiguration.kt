package io.kotest.core.factory

import io.kotest.core.config.Project
import io.kotest.core.sourceRef
import io.kotest.core.spec.*
import io.kotest.core.test.*

/**
 * A [TestFactoryConfiguration] provides a DSL to allow for easy creation of a
 * [TestFactory] when this class is the receiver of a lambda parameter.
 *
 * This class shouldn't be used directly, but as the base for a particular
 * layout style, eg [FunSpecTestFactoryConfiguration].
 */
abstract class TestFactoryConfiguration : TestConfiguration() {

   /**
    * This [factoryId] is a unique id across all factories. The id is used by
    * lifecycle callbacks declared in this factory to ensure they only operate
    * on tests declared in this factory.
    */
   val factoryId: TestFactoryId = TestFactoryId.next()

   /**
    * Contains the [DynamicTest]s that have been added to this configuration.
    */
   internal var tests = emptyList<DynamicTest>()

   internal fun resolvedDefaultConfig(): TestCaseConfig = defaultTestConfig ?: Project.testCaseConfig()

   // test lifecycle callbacks
   internal var beforeTests = emptyList<BeforeTest>()
   internal var afterTests = emptyList<AfterTest>()
   internal var beforeContainers = emptyList<BeforeContainer>()
   internal var afterContainers = emptyList<AfterContainer>()
   internal var beforeEaches = emptyList<BeforeEach>()
   internal var afterEaches = emptyList<AfterEach>()
   internal var beforeAnys = emptyList<BeforeAny>()
   internal var afterAnys = emptyList<AfterAny>()

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
    * Registers a new before-container callback to be executed before every [TestCase]
    * with type [TestType.Container].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   override fun beforeContainer(f: BeforeContainer) {
      beforeContainers = beforeContainers + f
   }

   /**
    * Registers a new after-container callback to be executed after every [TestCase]
    * with type [TestType.Container].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   override fun afterContainer(f: AfterContainer) {
      afterContainers = afterContainers + f
   }

   /**
    * Registers a new before-each callback to be executed before every [TestCase]
    * with type [TestType.Test].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   override fun beforeEach(f: BeforeEach) {
      beforeEaches = beforeEaches + f
   }

   /**
    * Registers a new after-each callback to be executed after every [TestCase]
    * with type [TestType.Test].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   override fun afterEach(f: AfterEach) {
      afterEaches = afterEaches + f
   }

   /**
    * Registers a new before-any callback to be executed before every [TestCase]
    * with type [TestType.Test] or [TestType.Container].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   override fun beforeAny(f: BeforeAny) {
      beforeAnys = beforeAnys + f
   }

   /**
    * Registers a new after-container callback to be executed after every [TestCase]
    * with type [TestType.Container] or [TestType.Test].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   override fun afterAny(f: AfterAny) {
      afterAnys = afterAnys + f
   }

   /**
    * Adds a new [DynamicTest] to this factory. When this factory is included
    * into a [Spec] these tests will be added to the spec as root [TestCase]s.
    */
   internal fun addDynamicTest(
       name: TestName,
       test: suspend TestContext.() -> Unit,
       config: TestCaseConfig,
       type: TestType,
   ) {
      require(tests.none { it.name == name }) { "Cannot add test with duplicate name $name" }
      this.tests = this.tests + DynamicTest(name, test, config, type, sourceRef(), factoryId)
   }
}
