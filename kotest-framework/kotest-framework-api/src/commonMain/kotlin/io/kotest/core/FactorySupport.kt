package io.kotest.core

import io.kotest.core.factory.DynamicTest
import io.kotest.core.factory.TestFactory

interface FactorySupport {

   /**
    * Include the tests from the given [TestFactory] in this spec or factory.
    */
   fun include(factory: TestFactory)

   /**
    * Includes the tests from the given [TestFactory] in this spec or factory, with the given
    * prefixed added to each of the test's name.
    */
   fun include(prefix: String, factory: TestFactory) {
      fun DynamicTest.addPrefix(): DynamicTest = copy(name = name.copy(name = "$prefix $name"))
      include(factory.copy(tests = factory.tests.map { it.addPrefix() }))
   }
}
