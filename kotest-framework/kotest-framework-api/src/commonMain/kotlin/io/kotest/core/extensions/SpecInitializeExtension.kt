package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An extension point that is invoked after a spec class has been created.
 *
 * Note: This extension is always invoked on a spec instance, regardless of whether that spec is
 * then ultimately skipped (no tests or all tests disabled). This extension is intended
 * for logic that must be invoked even when the spec is ultimately not used.
 *
 * Compare to [BeforeSpecExtension] which is only invoked if the spec is active and contains
 * at least one test.
 *
 * This extension is invoked once per spec instance. If a spec is instantiated multiple times,
 * then this extension will also be invoked multiple times.
 */
interface SpecInitializeExtension : Extension {

   /**
    * Accepts a [Spec] for processing before any tests are executed or enabled/disabled checks are performed.
    */
   fun initialize(spec: Spec)
}
