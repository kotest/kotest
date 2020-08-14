package io.kotest.core.spec

import io.kotest.core.factory.TestFactory
import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener

/**
 * Defines functions which can be invoked to set configuration options on tests
 * This is an alternative style to using [FunctionConfiguration].
 */
interface InlineConfiguration {

   /**
    * Adds [Tag]s to this spec or factory, which will be applied to each test case.
    *
    * When applied in a factory, only tests generated from that factory will have the tags applied.
    * When applied to a spec, all tests will have the tags applied.
    */
   fun tags(vararg tags: Tag)

   fun <T : TestListener> listener(listener: T): T

   fun listeners(vararg listener: TestListener)

   fun extensions(vararg extensions: TestCaseExtension)

   /**
    * Include the tests from the given [TestFactory] in this spec or factory.
    */
   fun include(factory: TestFactory)
}
