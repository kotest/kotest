package io.kotest.core

import io.kotest.core.spec.SpecConfiguration

enum class AssertionMode {
   Error, Warn, None
}

fun SpecConfiguration.resolvedAssertionMode(): AssertionMode =
   this.assertionMode ?: this.assertionMode() ?: AssertionMode.Error // todo add project mode
