package io.kotest.core

import io.kotest.SpecInterface

actual fun SpecInterface.resolvedAssertionMode(): AssertionMode = when (val v = this.assertionMode()) {
   null -> Project.assertionMode()
   else -> v
}
