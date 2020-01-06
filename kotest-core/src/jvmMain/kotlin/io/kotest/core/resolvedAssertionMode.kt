package io.kotest.core

import io.kotest.Project
import io.kotest.SpecClass

actual fun SpecClass.resolvedAssertionMode(): AssertionMode = when (val v = this.assertionMode()) {
   null -> Project.assertionMode()
   else -> v
}
