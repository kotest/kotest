package io.kotest.core

import io.kotest.SpecClass

actual fun SpecClass.resolvedAssertionMode(): AssertionMode = this.assertionMode() ?: AssertionMode.None
