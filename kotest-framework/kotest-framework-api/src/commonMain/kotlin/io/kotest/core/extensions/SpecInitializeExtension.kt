package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An [Extension] point that enables hooking into the spec factory lifecycle.
 */
interface SpecInitializeExtension : Extension {
   suspend fun initializeSpec(spec: Spec): Spec
}

