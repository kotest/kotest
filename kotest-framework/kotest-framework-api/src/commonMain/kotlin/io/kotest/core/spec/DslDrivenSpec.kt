package io.kotest.core.spec

import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.Tuple2
import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.addPrefix
import io.kotest.core.factory.createTestCases
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.createRootTestCase
import kotlin.reflect.KClass

/**
 * Base class for specs that allow for registration of tests via the DSL.
 */
abstract class DslDrivenSpec : Spec() {

   /**
    * Contains the root [TestCase]s used in this spec.
    */
   private var rootTestCases = emptyList<TestCase>()

   override fun materializeRootTests(): List<RootTest> {
      return rootTestCases.withIndex().map { RootTest(it.value, it.index) }
   }

   /**
    * Include the tests, listeners and extensions from the given [TestFactory] in this spec.
    * Tests are added in order from where this include was invoked using configuration and
    * settings at the time the method was invoked.
    */
   fun include(factory: TestFactory) {
      factory.createTestCases(this::class.toDescription(), this).forEach { addRootTest(it) }
      listeners(factory.listeners)
   }

   /**
    * Includes the tests from the given [TestFactory] in this spec or factory, with the given
    * prefixed added to each of the test's name.
    */
   fun include(prefix: String, factory: TestFactory) {
      include(factory.copy(tests = factory.tests.map { it.addPrefix(prefix) }))
   }

   /**
    * Registers a callback that will execute after all tests in this spec have completed.
    * This is a convenience method for creating a [TestListener] and registering it to only
    * fire for this spec.
    */
   fun finalizeSpec(f: FinalizeSpec) {
      configuration.registerListener(object : TestListener {
         override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
            if (kclass == this@DslDrivenSpec::class) {
               f(Tuple2(kclass, results))
            }
         }
      })
   }

   /**
    * Registers a callback that will execute after all specs have completed.
    * This is a convenience method for creating a [ProjectListener] and registering it.
    */
   fun afterProject(f: AfterProject) {
      configuration.registerListener(object : ProjectListener {
         override suspend fun afterProject() {
            f()
         }
      })
   }

   @Deprecated("this makes no sense")
   fun aroundSpec(aroundSpecFn: AroundSpecFn) {
      extension(object : SpecExtension {
         override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
            aroundSpecFn(Tuple2(spec, process))
         }
      })
   }

   /**
    * Adds a new root-level [TestCase] to this [Spec].
    */
   override fun addTest(
      name: DescriptionName.TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      addRootTest(createRootTestCase(this, name, test, config, type))
   }

   /**
    * Adds a new root-level [TestCase] to this [Spec].
    */
   private fun addRootTest(testCase: TestCase) {
      if (rootTestCases.any { it.description.name == testCase.description.name }) throw DuplicatedTestNameException(testCase.description.name)
      rootTestCases = rootTestCases + testCase
   }
}
