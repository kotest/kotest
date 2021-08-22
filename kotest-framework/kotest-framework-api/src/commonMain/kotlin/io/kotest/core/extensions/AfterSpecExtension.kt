package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An extension point that is invoked after all tests have completed in a spec instance.
 *
 * This extension is useful for executing cleanup logic that should only be invoked
 * if the spec was active.
 *
 * This extension is invoked once per active spec instance. If a spec is instantiated and used
 * multiple times, then this extension will also be invoked multiple times.
 */
interface AfterSpecExtension : Extension {
   fun afterSpec(spec: Spec)
}
