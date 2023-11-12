package io.kotest.engine.test.names

import io.kotest.core.test.TestCase

fun FallbackDisplayNameFormatter.formatTestPath(testCase: TestCase, separator: String): String {
   return when (val parent = testCase.parent) {
      null -> format(testCase)
      else -> formatTestPath(parent, separator) + separator + format(testCase)
   }
}
