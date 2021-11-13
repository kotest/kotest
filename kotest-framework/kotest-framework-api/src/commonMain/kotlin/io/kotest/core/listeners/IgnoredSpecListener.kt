package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Invoked if a [Spec] was skipped.
 *
 * This can be because the spec was annotatd with @Ignored, or because the spec
 * failed to pass an @EnabledIf predicate, and so on.
 */
interface IgnoredSpecListener : Extension {
   suspend fun ignoredSpec(kclass: KClass<*>, reason: String?)
}
