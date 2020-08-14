package io.kotest.engine.spec

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

/**
 * Returns the resolved listeners for a given [AbstractSpec].
 * That is, the listeners defined directly on the spec, listeners generated from the
 * callback-dsl methods, and listeners defined in any included test factories.
 */
fun Spec.resolvedTestListeners(): List<TestListener> {
   return when (this) {
      is AbstractSpec -> this._listeners + this.listeners() + this.functionOverrideCallbacks() + factories.flatMap { it.listeners }
      else -> emptyList()
   }
}
