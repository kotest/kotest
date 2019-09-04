package io.kotlintest

enum class AssertionMode {
   Error, Warn, None
}

expect fun Spec.resolvedAssertionMode(): AssertionMode
