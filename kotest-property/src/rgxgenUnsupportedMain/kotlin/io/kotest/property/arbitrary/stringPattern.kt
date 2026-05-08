package io.kotest.property.arbitrary

import io.kotest.property.Arb

actual fun Arb.Companion.stringPattern(pattern: String): Arb<String> {
   throw UnsupportedOperationException(
      "Arb.stringPattern is not supported on Android Native targets; " +
         "kotlin-rgxgen 0.0.3 does not publish a binary for them."
   )
}
