package io.kotest.engine.spec

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

/**
 * Returns the resolved listeners for a given [AbstractSpec].
 * That is, the listeners defined directly on the spec, listeners generated from the
 * callback-dsl methods, and listeners defined in any included test factories.
 */
fun Spec.resolvedTestListeners(): List<TestListener> {
   return listeners() + // listeners defined by overriding the listeners function
      this.functionOverrideCallbacks() // listeners from the overrides
   // + factories.flatMap { it.listeners }
}
