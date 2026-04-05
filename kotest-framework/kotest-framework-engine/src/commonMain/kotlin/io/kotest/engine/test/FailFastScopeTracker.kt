package io.kotest.engine.test

import io.kotest.core.descriptors.Descriptor
import kotlin.coroutines.CoroutineContext

/**
 * A coroutine context element that tracks which failfast scopes have been triggered within a spec.
 *
 * A "failfast scope" is identified by the [Descriptor.TestDescriptor] of the nearest ancestor
 * test that has `failfast = true` explicitly in its own config. When `null`, it represents the
 * spec-level (or project-level) failfast scope.
 *
 * This lives in the coroutine context rather than in [io.kotest.engine.spec.interceptor.SpecContext]
 * so that each spec gets an isolated tracker without coupling state to the spec context.
 */
internal class FailFastScopeTracker : CoroutineContext.Element {

   companion object Key : CoroutineContext.Key<FailFastScopeTracker>

   override val key: CoroutineContext.Key<*> = Key

   // null key = spec/project-level failfast (no test ancestor owns the scope)
   private val failedScopes = mutableSetOf<Descriptor.TestDescriptor?>()

   fun markFailed(scope: Descriptor.TestDescriptor?) {
      failedScopes.add(scope)
   }

   fun hasFailed(scope: Descriptor.TestDescriptor?): Boolean = scope in failedScopes
}
