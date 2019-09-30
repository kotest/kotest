package io.kotest

actual fun Spec.resolvedAssertionMode(): AssertionMode = this.assertionMode() ?: AssertionMode.None
