package io.kotest.engine.test

import io.kotest.core.descriptors.Descriptor
import io.kotest.engine.spec.threadSafeMap
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
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
@OptIn(ExperimentalAtomicApi::class)
internal class FailFastScopeTracker : CoroutineContext.Element {

   companion object Key : CoroutineContext.Key<FailFastScopeTracker>

   override val key: CoroutineContext.Key<*> = Key

   // A single tracker instance is shared across tests that may run concurrently (testExecutionMode
   // concurrency > 1), so the backing storage must be thread-safe to avoid races between markFailed
   // and hasFailed. A concurrent map (used here as a set via its keys) provides this; since it does
   // not permit null keys, the spec/project-level scope (null) is tracked separately by an atomic.
   private val failedScopes = threadSafeMap<Descriptor.TestDescriptor, Unit>()

   // tracks the spec/project-level failfast scope (no test ancestor owns the scope)
   private val specLevelFailed = AtomicBoolean(false)

   fun markFailed(scope: Descriptor.TestDescriptor?) {
      if (scope == null) specLevelFailed.store(true) else failedScopes[scope] = Unit
   }

   fun hasFailed(scope: Descriptor.TestDescriptor?): Boolean =
      if (scope == null) specLevelFailed.load() else failedScopes.containsKey(scope)
}
