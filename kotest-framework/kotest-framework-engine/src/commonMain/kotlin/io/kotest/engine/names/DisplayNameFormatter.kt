package io.kotest.engine.names

import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

/**
 * A [DisplayNameFormatter] is responsible for formatting test and spec names
 * for display or reporting purposes.
 */
interface DisplayNameFormatter {

   /**
    * Returns a formatted name for a test.
    */
   fun format(testCase: TestCase): String?

   /**
    * Returns a formatted name for a spec.
    *
    * The returned value should only be the name of the spec, without any package or nested class information.
    *
    * The test engine will prepend the package name where appropriate.
    *
    * The default when not specified is to use the simple class name.
    */
   fun format(kclass: KClass<*>): String?
}
