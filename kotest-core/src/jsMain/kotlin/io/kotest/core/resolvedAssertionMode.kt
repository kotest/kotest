package io.kotest.core

import io.kotest.SpecInterface

actual fun SpecInterface.resolvedAssertionMode(): AssertionMode = this.assertionMode() ?: AssertionMode.None
