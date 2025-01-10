package io.kotest.assertions

import io.kotest.assertions.print.print

actual fun describeTypedMismatch(expected: Any?, actual: Any?): String {
   val expectedType = expected?.let { it::class.qualifiedName } ?: return ""
   val actualType = actual?.let { it::class.qualifiedName } ?: return ""
   val expectedPrintValue = expected.print().value
   val actualPrintValue = actual.print().value
   return if(expectedPrintValue == actualPrintValue) {
      "\nExpected type $expectedType, but was $actualType"
   } else
      ""
}
