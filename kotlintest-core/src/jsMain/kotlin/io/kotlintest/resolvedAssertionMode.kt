package io.kotlintest

actual fun Spec.resolvedAssertionMode(): AssertionMode = this.assertionMode ?: AssertionMode.None
