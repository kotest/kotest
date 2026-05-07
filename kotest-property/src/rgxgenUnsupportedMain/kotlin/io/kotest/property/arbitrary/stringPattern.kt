package io.kotest.property.arbitrary

import io.kotest.property.Arb

actual fun Arb.Companion.stringPattern(pattern: String): Arb<String> {
   throw UnsupportedOperationException(
      "Arb.stringPattern is not supported on this Kotlin/Native or Wasm target; " +
         "kotlin-rgxgen does not publish a binary for it."
   )
}
