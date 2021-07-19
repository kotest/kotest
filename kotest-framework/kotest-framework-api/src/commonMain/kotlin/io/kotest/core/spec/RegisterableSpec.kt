package io.kotest.core.spec

import io.kotest.core.execution.ExecutionContext
import io.kotest.core.factory.FactoryId
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.addPrefix
import io.kotest.core.plan.Descriptor
import io.kotest.core.plan.Source
import io.kotest.core.plan.TestName
import io.kotest.core.source
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestNameFormatter
import io.kotest.core.test.TestType

/**
 * A [Spec] that allows tests to be registered programatically.
 */
abstract class RegisterableSpec : Spec() {

   /**
    * Contains the [TestCase]s registered in this spec.
    */
   internal val tests = mutableListOf<TopLevelTest>()

   override fun materializeRootTests(context: ExecutionContext): List<RootTest> {
      val formatter = TestNameFormatter(context.configuration)
      return tests
         .map {
            val tags = this.tags() + it.config.tags
            TestCase(
               Descriptor.SpecDescriptor(this).append(it.name, formatter.format(it.name, tags), it.type),
               this,
               null,
               it.test,
               it.type,
               it.source,
               it.config,
               null
            )
         }
         .withIndex()
         .map { RootTest(it.value, it.index) }
   }

   override fun testNames(): List<TestName> = tests.map { it.name }
   
   /**
    * Adds a new top-level [TestCase] to this [Spec].
    */
   override fun addTest(
      name: TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType,
      source: Source?,
      factoryId: FactoryId?,
   ) {
      addTest(TopLevelTest(name, type, config, source(), test))
   }

   internal fun addTest(test: TopLevelTest) {
      tests.add(test)
   }

   /**
    * Include the tests, listeners and extensions from the given [TestFactory] in this spec.
    *
    * Tests are added in order from where this include was invoked using configuration and
    * settings at the time the method was invoked.
    *
    * All tests included from a factory on a spec are included as top level tests regardless
    * of where that factory was invoked.
    */
   fun include(factory: TestFactory) {
      factory.tests.forEach {
         addTest(
            it.name,
            it.test,
            it.config,
            it.type,
            it.source,
            it.factoryId,
         )
      }
      listeners(factory.listeners)
   }

   /**
    * Includes the tests from the given [TestFactory] in this spec or factory, with the given
    * prefix added to each of the test's name.
    */
   fun include(prefix: String, factory: TestFactory) {
      include(factory.copy(tests = factory.tests.map { it.addPrefix(prefix) }))
   }
}

data class TopLevelTest(
   val name: TestName,
   val type: TestType,
   val config: TestCaseConfig,
   val source: Source?,
   val test: suspend TestContext.() -> Unit,
)
