package io.kotest.core.factory

import io.kotest.core.TestConfiguration
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.style.scopes.RootScope

/**
 * A [TestFactoryConfiguration] extends [TestConfiguration] with the ability to register
 * [DynamicRootTest]s. This class shouldn't be used directly, but as the base for a particular
 * layout style, e.g. [io.kotest.core.spec.style.FunSpecTestFactoryConfiguration].
 */
abstract class TestFactoryConfiguration : TestConfiguration(), RootScope {

   /**
    * This [factoryId] is a unique id across all factories. The id is used by
    * lifecycle callbacks declared in this factory to ensure they only operate
    * on tests declared in this factory.
    */
   val factoryId: FactoryId = FactoryId.next()

   /**
    * Contains the [RootTest]s that have been added to this factory.
    */
   internal var tests = emptyList<RootTest>()

   override fun add(test: RootTest) {
      tests = tests + test
   }

   /**
    * Include the tests, listeners and extensions from the given [TestFactory] in this factory.
    * Tests are added in order from where this include was invoked using configuration and
    * settings at the time the method was invoked.
    */
   fun include(factory: TestFactory) {
      factory.tests.forEach { add(it) }
      factory.configuration.setParentConfiguration(this)
      register(factory.extensions)
   }
}
