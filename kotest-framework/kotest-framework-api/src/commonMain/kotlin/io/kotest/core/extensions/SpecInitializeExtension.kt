package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An extension point that is invoked after a spec has been retrieved from a [io.kotest.core.spec.SpecRef].
 *
 * Note: This extension is always invoked on a spec instance, regardless of whether that spec is
 * then ultimately skipped (no tests or all tests disabled). This extension is intended
 * for logic that must be invoked even when the spec is ultimately not used.
 *
 * This extension is invoked once per spec instance. If a spec is instantiated multiple times,
 * then this extension will also be invoked multiple times.
 *
 * Note: This extension differs from [io.kotest.core.listeners.SpecInstantiationListener], in that
 * this extension is invoked on all platforms when a spec factory returns a spec, whereas the
 * instantiation listener is only invoked when the spec is created reflectively on the JVM.
 */
interface SpecInitializeExtension : Extension {

   /**
    * Accepts a [Spec] for processing before any tests are executed or enabled/disabled checks are performed.
    */
   fun initialize(spec: Spec)
}
