package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An [Extension] point that enables hooking into the spec factory lifecycle.
 */
interface SpecCreatedListener : Extension {
   suspend fun onSpecCreated(spec: Spec)
}

