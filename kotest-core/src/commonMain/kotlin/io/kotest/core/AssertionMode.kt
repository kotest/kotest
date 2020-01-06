package io.kotest.core

import io.kotest.SpecClass

enum class AssertionMode {
   Error, Warn, None
}

expect fun SpecClass.resolvedAssertionMode(): AssertionMode
