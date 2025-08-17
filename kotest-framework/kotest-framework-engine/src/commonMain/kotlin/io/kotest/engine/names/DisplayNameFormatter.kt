package io.kotest.engine.names

import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

/**
 * A [DisplayNameFormatter] is an responsible for formatting test and spec names
 * for display or reporting purposes.
 */
interface DisplayNameFormatter {

   /**
    * Returns a formatted name for a test.
    */
   fun format(testCase: TestCase): String

   /**
    * Returns a formatted name for a spec.
    *
    */
   fun format(kclass: KClass<*>): String
}
