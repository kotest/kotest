package io.kotest.engine.names

import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

/**
 * Returns formatted spec and test names for display or reporting purposes.
 */
interface DisplayNameFormatter {

   /**
    * Returns a formatted name for a test.
    */
   fun format(testCase: TestCase): String?

   /**
    * Returns a formatted name for a spec class.
    */
   fun format(kclass: KClass<*>): String?
}
