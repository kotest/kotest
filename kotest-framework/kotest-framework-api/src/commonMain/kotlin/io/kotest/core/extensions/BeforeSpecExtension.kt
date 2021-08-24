package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An extension point that is invoked before any tests in an active spec.
 *
 * This extension is useful for executing setup logic that should only be invoked
 * if the spec is active.
 *
 * This extension is invoked once per active spec instance. If a spec is instantiated and used
 * multiple times, then this extension will also be invoked multiple times.
 */
interface BeforeSpecExtension : Extension {
   fun beforeSpec(spec: Spec): Unit = Unit
}
