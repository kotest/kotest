package io.kotest.core.extensions

import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

interface DisplayNameFormatterExtension {
   fun formatter(): DisplayNameFormatter
}

/**
 * Returns formatted spec and test names for display or reporting purposes.
 */
interface DisplayNameFormatter {

   /**
    * Returns a formatted name for a test.
    */
   fun format(testCase: TestCase): String

   /**
    * Returns a formatted name for a spec class.
    */
   fun format(kclass: KClass<*>): String
}

fun DisplayNameFormatter.formatTestPath(testCase: TestCase, separator: String): String {
   return when (val parent = testCase.parent) {
      null -> format(testCase)
      else -> format(parent) + separator + format(testCase)
   }
}
