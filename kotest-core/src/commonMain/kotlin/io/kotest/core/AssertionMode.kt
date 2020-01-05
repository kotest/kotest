package io.kotest.core

import io.kotest.SpecInterface

enum class AssertionMode {
   Error, Warn, None
}

expect fun SpecInterface.resolvedAssertionMode(): AssertionMode
