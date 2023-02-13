package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Invoked if a [Spec] was skipped.
 *
 * This can be because the spec was annotated with [@Ignored][io.kotest.core.annotation.Ignored],
 * or because the spec failed to pass an [@EnabledIf][io.kotest.core.annotation.EnabledIf] predicate, and so on.
 */
interface IgnoredSpecListener : Extension {
   suspend fun ignoredSpec(kclass: KClass<*>, reason: String?)
}
