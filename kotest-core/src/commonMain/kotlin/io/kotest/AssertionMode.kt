package io.kotest

enum class AssertionMode {
   Error, Warn, None
}

expect fun Spec.resolvedAssertionMode(): AssertionMode
