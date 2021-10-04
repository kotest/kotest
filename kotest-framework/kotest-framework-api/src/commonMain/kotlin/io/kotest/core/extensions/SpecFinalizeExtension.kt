package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An extension point that is used to post-process a spec class once all (if any) tests have
 * complete and the spec instance will no longer be used.
 *
 * Note: This extension is invoked regardless of whether the spec had any tests, or if the spec
 * was disabled. This extension is intended to be used for cleanup logic which must run anytime
 * a spec is instantiated, regardless of whether the spec was then ultimately executed.
 *
 * This extension is invoked once per spec instance. If a spec is instantiated multiple times,
 * then this extension will also be invoked multiple times.
 */
interface SpecFinalizeExtension : Extension {

   /**
    * Accepts a [Spec] for processing before that spec is completed.
    */
   fun finalizeSpec(spec: Spec)
}
