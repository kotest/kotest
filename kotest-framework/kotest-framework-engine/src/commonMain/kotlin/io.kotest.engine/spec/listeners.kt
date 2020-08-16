package io.kotest.engine.spec

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

/**
 * Returns the resolved listeners for a given [Spec].
 *
 * This is, listeners returned from the `listener` function override, listeners assigned via
 * the inline function, listeners generated from the lambda functions, and listeners
 * from the specific function overrides.
 */
fun Spec.resolvedTestListeners(): List<TestListener> {
   return listeners() + // listeners defined by overriding the listeners function
      this.registeredListeners() + // listeners added via the inline callbacks
      this.functionOverrideCallbacks() // listeners from the overrides
}
