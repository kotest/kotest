package io.kotest

actual fun Spec.resolvedAssertionMode(): AssertionMode = when (val v = this.assertionMode()) {
   null -> Project.assertionMode()
   else -> v
}
